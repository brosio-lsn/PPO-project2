package ch.epfl.javelo.gui;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.projection.PointWebMercator;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;


/**
 * Manages the display of the map
 * @author Louis ROCHE (345620)
 * @author Ambroise AIGUEPERSE (341890)
 */
public final class BaseMapManager {
    /**
     * TileManager to be used to load each image
     */
    private final TileManager tileManager;
    /**
     * WayPointsManager to be used to manage Waypoints
     */
    private final WaypointsManager waypointsManager;
    /**
     * property to be used to get the parameters from which we can observe the map
     */
    private final ObjectProperty<MapViewParameters> mapViewParametersProp;
    /**
     * canvas to be drawn on.
     */
    private final Canvas canvas;
    /**
     * pane to put the canvas on.
     */
    private final Pane pane;
    /**
     * boolean telling whether a redraw of the map is needed.
     */
    private boolean redrawNeeded;
    /**
     * number of pixels per tile
     */
    private final static int PIXELS_PER_TILE = 256;
    /**
     * minimum zoom level
     */
    private final static int ZOOM_LEVEl_MIN = 8;
    /**
     * maximum zoom level
     */
    private final static int ZOOM_LEVEL_MAX = 19;
    /**
     * property recording the coordinates of the mouse on the last event it indulged in.
     */
    private final ObjectProperty<Point2D> mouseOnLastEvent;
    /**
     * Number of additional iterations needed to draw the image on the X-axis of the canvas
     */
    private final static int X_ITERATIONS = 2 * PIXELS_PER_TILE;
    /**
     * Number of additional iterations needed to draw the image on the Y-axis of the canvas
     */
    private final static int Y_ITERATIONS = PIXELS_PER_TILE;
    /**
     * Time to wait before scrolling again
     */
    private final static int TIME_TO_WAIT = 200;



    /**
     * Constructor of the BaseMapManager class.
     *
     * @param tileManager           tileManager to be used to draw the tiles and see whether they are valid
     * @param waypointsManager      waypointsManager to be used to add/remove waypoints
     * @param mapViewParametersProp property to use to observe the mapViewParameters.
     */
    public BaseMapManager(TileManager tileManager, WaypointsManager waypointsManager, ObjectProperty<MapViewParameters> mapViewParametersProp) {
        this.waypointsManager = waypointsManager;
        this.mapViewParametersProp = mapViewParametersProp;
        this.tileManager = tileManager;
        canvas = new Canvas();
        pane = new Pane(canvas);
        mouseOnLastEvent = new SimpleObjectProperty<>();
        installBindings();
        installListeners();
        createHandlers();
    }

    /**
     * returns the pane of the class
     *
     * @return returns the pane of the class
     */
    public Pane pane() {
        return pane;
    }

    /**
     * redraws the map if it is needed - if redrawOnNextPulse() has been called
     */
    private void redrawIfNeeded() {
        if (!redrawNeeded) return;
        redrawNeeded = false;
        drawMap();
    }

    /**
     * draws the map on the canvas
     */
    private void drawMap() {
        double xTopLeft = mapViewParametersProp.get().topLeft().getX();
        double yTopLeft = mapViewParametersProp.get().topLeft().getY();
        int zoomLevel = mapViewParametersProp.get().zoomLevel();
        GraphicsContext context = canvas.getGraphicsContext2D();
        Image imageToDraw;
        boolean canDraw = true;
        for (int xOnCanvas = 0; xOnCanvas <= canvas.getWidth()+X_ITERATIONS; xOnCanvas += PIXELS_PER_TILE) {
            for (int yOnCanvas = 0; yOnCanvas <= canvas.getHeight()+Y_ITERATIONS; yOnCanvas += PIXELS_PER_TILE) {
                int yTileIndex = (int) (yTopLeft + yOnCanvas) / PIXELS_PER_TILE;
                int xTileIndex = (int) (xTopLeft + xOnCanvas) / PIXELS_PER_TILE;
                try {
                    imageToDraw = tileManager.imageForTileAt(new TileManager.TileId(zoomLevel, xTileIndex, yTileIndex));
                } catch (Exception e) {
                    canDraw = false;
                    break;
                }
                if (canDraw) {
                    context.drawImage(imageToDraw, xTileIndex * PIXELS_PER_TILE - xTopLeft, yTileIndex * PIXELS_PER_TILE - yTopLeft);
                }
                canDraw = true;
            }
        }
    }

    /**
     * tells the program to redraw the canvas on the next pulse - and redrawIfNeeded will do it.
     */
    private void redrawOnNextPulse() {
        redrawNeeded = true;
        Platform.requestNextPulse();
    }

    /**
     * creates the event handlers bound to the pane, and makes them handle each event accordingly
     * List of events :
     * - mouse scroll -> zoom/dezoom
     * - mouse press -> initializes the dragging of the map
     * - mouse drag -> drags the map around
     * - mouse release -> stops the dragging
     * - mouse click -> puts a waypoint on the place the mouse clicked on.
     */
    private void createHandlers() {

        SimpleLongProperty minScrollTime = new SimpleLongProperty();
        pane.setOnScroll(e -> {
            if (e.getDeltaY() == 0) return;
            mouseOnLastEvent.set(new Point2D(e.getX(), e.getY()));
            long currentTime = System.currentTimeMillis();
            if (currentTime < minScrollTime.get()) return;
            minScrollTime.set(currentTime + TIME_TO_WAIT);
            double zoomDelta = Math.signum(e.getDeltaY());
            int currentZoomLevel = mapViewParametersProp.get().zoomLevel();
            int newZoomLevel = (int) Math2.clamp(ZOOM_LEVEl_MIN, currentZoomLevel + zoomDelta, ZOOM_LEVEL_MAX);
            PointWebMercator corner = mapViewParametersProp.get().pointAt(e.getX(), e.getY());
            mapViewParametersProp.set(new MapViewParameters(newZoomLevel
                    , corner.xAtZoomLevel(newZoomLevel) - e.getX()
                    , corner.yAtZoomLevel(newZoomLevel) - e.getY()));
        });
        pane.setOnMousePressed(event -> {
            mouseOnLastEvent.set(new Point2D(event.getX(), event.getY()));
        });
        pane.setOnMouseDragged(event -> {
            double deltaX = event.getX() - mouseOnLastEvent.get().getX();
            double deltaY = event.getY() - mouseOnLastEvent.get().getY();
            mapViewParametersProp.set(mapViewParametersProp.get().withMinXY(mapViewParametersProp.get().x() - deltaX,
                    mapViewParametersProp.get().y() - deltaY));
            mouseOnLastEvent.set(new Point2D(event.getX(), event.getY()));
        });
        pane.setOnMouseReleased(event -> mouseOnLastEvent.set(new Point2D(event.getX(), event.getY())));
        pane.setOnMouseClicked(event -> {
            mouseOnLastEvent.set(new Point2D(event.getX(), event.getY()));
            if (event.isStillSincePress()) waypointsManager.addWayPoint(event.getX(), event.getY());
        });
    }

    /**
     * installs the required binding so that the size of the canvas fits that of the pane
     */
    private void installBindings() {
        canvas.widthProperty().bind(pane.widthProperty());
        canvas.heightProperty().bind(pane.heightProperty());
    }

    /**
     * install the required listeners to redraw the canvas when an event happens
     * Events that require the canvas to update itself :
     * - the scene of the canvas has changed
     * - the width of the canvas has changed
     * - the height of the canvas has changed
     * - the MapViewParameters property of this baseMapManager has changed
     */
    private void installListeners() {
        canvas.sceneProperty().addListener((p, oldS, newS) -> {
            assert oldS == null;
            newS.addPreLayoutPulseListener(this::redrawIfNeeded);
        });
        canvas.heightProperty().addListener((observable, oldValue, newValue) -> redrawOnNextPulse());
        canvas.widthProperty().addListener((o, oV, nV) -> redrawOnNextPulse());
        mapViewParametersProp.addListener((o, oV, nV) -> redrawOnNextPulse());
    }
}

