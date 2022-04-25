package ch.epfl.javelo.gui;

import ch.epfl.javelo.Math2;
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
    private final ObjectProperty<MapViewParameters> property;
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
    boolean redrawNeeded;
    /**
     * number of pixels per tile
     */
    private final static int PIXELS_PER_TILE = 256;
    /**
     * scaling factor a zoom engenders. Zooming in of 1 level multiplies the number of tiles by 4.
     */
    private final static int ZOOM_SCALING_FACTOR = 2;
    /**
     * minimum zoom level
     */
    private final static int ZOOM_LEVEl_MIN = 8;
    /**
     * maximum zoom level
     */
    private final static int ZOOM_LEVEL_MAX = 19;
    /**
     * xTopLeft : x coordinate of the top left corner of the map
     * yTopLeft : y coordinate of the top left corner of the map
     * zoomLevel : zoomLevel
     */
    private int xTopLeft, yTopLeft, zoomLevel;
    /**
     * property recording the coordinates of the mouse on the last event it indulged in.
     */
    private final ObjectProperty<Point2D> mouseOnLastEvent;
    /**
     * context which will be associated with that of the canvas
     */
    private GraphicsContext context;
    /**
     * Number of iterations needed to draw the image on the X-axis of the canvas
     */
    private final int X_ITERATIONS = 4 * PIXELS_PER_TILE;
    /**
     * Number of iterations needed to draw the image on the Y-axis of the canvas
     */
    private final int Y_ITERATIONS = 3 * PIXELS_PER_TILE;

    /**
     * Constructor of the BaseMapManager class.
     *
     * @param tileManager      tileManager to be used to draw the tiles and see whether they are valid
     * @param waypointsManager waypointsManager to be used to add/remove waypoints
     * @param property         property to use to observe the mapViewParameters.
     */

    public BaseMapManager(TileManager tileManager, WaypointsManager waypointsManager, ObjectProperty<MapViewParameters> property) {
        this.waypointsManager = waypointsManager;
        this.property = property;
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
     * redraws the canvas if it is needed - if redrawOnNextPulse() has been called
     */
    private void redrawIfNeeded() {
        xTopLeft = (int) property.get().topLeft().getX();
        yTopLeft = (int) property.get().topLeft().getY();
        zoomLevel = property.get().zoomLevel();
        if (!redrawNeeded) return;
        redrawNeeded = false;
        context = canvas.getGraphicsContext2D();
        Image imageToDraw;
        boolean canDraw = true;
        for (int x = 0; x <= X_ITERATIONS; x += PIXELS_PER_TILE) {
            for (int y = 0; y <= Y_ITERATIONS; y += PIXELS_PER_TILE) {
                int yTileIndex = (yTopLeft + y) / PIXELS_PER_TILE;
                int xTileIndex = (xTopLeft + x) / PIXELS_PER_TILE;
                try {
                    imageToDraw = tileManager.imageForTileAt(new TileManager.TileId(zoomLevel, xTileIndex, yTileIndex));
                } catch (Exception e) {
                    canDraw = false;
                    if (e.getClass() == NullPointerException.class) System.out.println("nullPointerException");
                    System.out.println(e.getMessage());
                    break;
                }
                if (canDraw) {
                    System.out.println(xTileIndex + "-" + yTileIndex + " -> coordinates :  \nx =" + (xTileIndex * PIXELS_PER_TILE - xTopLeft) + " \ny = " + (yTileIndex * PIXELS_PER_TILE - yTopLeft));
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
            mouseOnLastEvent.set(new Point2D(e.getX(), e.getY()));
            double eventXCoordinate = xTopLeft + e.getX();
            double eventYCoordinate = yTopLeft + e.getY();
            long currentTime = System.currentTimeMillis();
            if (currentTime < minScrollTime.get()) return;
            minScrollTime.set(currentTime + 250);
            double zoomDelta = Math.signum(e.getDeltaY());
            if (!((zoomLevel == ZOOM_LEVEL_MAX && zoomDelta > 0) || (zoomLevel == ZOOM_LEVEl_MIN && zoomDelta < 0))) {
                zoomLevel = (int) Math2.clamp(ZOOM_LEVEl_MIN, zoomLevel + zoomDelta, ZOOM_LEVEL_MAX);
                double scalingFactor = zoomDelta > 0 ? ZOOM_SCALING_FACTOR : (double) 1 / ZOOM_SCALING_FACTOR;
                xTopLeft = (int) ((xTopLeft + e.getX()) * scalingFactor);
                yTopLeft = (int) (scalingFactor * (yTopLeft + e.getY()));
                context.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
                property.set(new MapViewParameters(zoomLevel, xTopLeft - e.getX(), yTopLeft - e.getY()));
            }
        });
        pane.setOnMousePressed(event -> {
            mouseOnLastEvent.set(new Point2D(event.getX(), event.getY()));
        });
        //TODO dragging not smooth at all
        pane.setOnMouseDragged(event -> {
            double deltaX = event.getX() - mouseOnLastEvent.get().getX();
            double deltaY = event.getY() - mouseOnLastEvent.get().getY();
            property.set(property.get().withMinXY(xTopLeft - deltaX, yTopLeft - deltaY));
            mouseOnLastEvent.set(new Point2D(event.getX(), event.getY()));
        });
        pane.setOnMouseReleased(event -> {
            mouseOnLastEvent.set(new Point2D(event.getX(), event.getY()));
        });
        pane.setOnMouseClicked(event -> {
            if (mouseOnLastEvent.get() == null) mouseOnLastEvent.set(new Point2D(event.getX(), event.getY()));
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
        property.addListener((o, oV, nV) -> redrawOnNextPulse());
    }

}

