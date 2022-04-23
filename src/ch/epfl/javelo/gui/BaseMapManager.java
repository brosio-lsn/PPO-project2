package ch.epfl.javelo.gui;

import ch.epfl.javelo.Math2;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.effect.Light;
import javafx.scene.image.Image;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;

import java.io.IOException;

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
     * Constructor of the BaseMapManager class.
     * @param tileManager tileManager to be used to draw the tiles and see whether they are valid
     * @param waypointsManager waypointsManager to be used to add/remove waypoints
     * @param property property to use to observe the mapViewParameters.
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
        GraphicsContext context = canvas.getGraphicsContext2D();
        Image imageToDraw;
        boolean canDraw = true;
        for (int x = 0; x <= canvas.getWidth() + 2*PIXELS_PER_TILE; x += PIXELS_PER_TILE) {
            for (int y = 0; y <= canvas.getHeight() + 2*PIXELS_PER_TILE; y += PIXELS_PER_TILE) {
                int yTileIndex = (yTopLeft + y) / PIXELS_PER_TILE;
                int xTileIndex = (xTopLeft + x) / PIXELS_PER_TILE;
                try {
                    imageToDraw = tileManager.imageForTileAt(new TileManager.TileId(zoomLevel, xTileIndex, yTileIndex));
                } catch (Exception e) {
                    canDraw = false;
                    System.out.println("frero tu pousses");
                    break;
                }
                if (canDraw) {
                    System.out.println(xTileIndex + "-" + yTileIndex + " -> coordinates :  \nx =" + (xTileIndex * PIXELS_PER_TILE-xTopLeft) + " \ny = "  +(yTileIndex * PIXELS_PER_TILE - yTopLeft));
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
            e.consume();
            long currentTime = System.currentTimeMillis();
            if (currentTime < minScrollTime.get()) return;
            minScrollTime.set(currentTime + 250);
            double zoomDelta = Math.signum(e.getDeltaY());
            zoomLevel = (int) Math2.clamp(ZOOM_LEVEl_MIN, zoomLevel + zoomDelta, ZOOM_LEVEL_MAX);
            double scalingFactor = zoomDelta > 0 ? ZOOM_SCALING_FACTOR : (double)1/ZOOM_SCALING_FACTOR;
            xTopLeft *= scalingFactor;
            yTopLeft *= scalingFactor;
            MapViewParameters aaa = property.get();
            System.out.println(aaa);
            System.out.println(xTopLeft);
            property.set(new MapViewParameters(zoomLevel, xTopLeft, yTopLeft));
        });
        pane.setOnMousePressed(event -> {
           mouseOnLastEvent.set(new Point2D(event.getX(), event.getY()));

        });
        pane.setOnMouseDragged(event -> {
            double deltaX = event.getX() - mouseOnLastEvent.get().getX();
            double deltaY = event.getY() - mouseOnLastEvent.get().getY();
            property.set(property.get().withMinXY(xTopLeft - deltaX, yTopLeft - deltaY));
        });
        pane.setOnMouseReleased(event -> {
            double deltaX = event.getX() - mouseOnLastEvent.get().getX();
            double deltaY = event.getY() - mouseOnLastEvent.get().getY();
            mouseOnLastEvent.get().add(deltaX, deltaY);
        });
        pane.setOnMouseClicked(event -> {
            if (mouseOnLastEvent.get() == null) mouseOnLastEvent.set(new Point2D(event.getX(), event.getY()));
            if (event.isStillSincePress()) waypointsManager.addWayPoint(event.getX(), event.getY());
        });
    }
    private void installBindings() {
        canvas.widthProperty().bind(pane.widthProperty());
        canvas.heightProperty().bind(pane.heightProperty());
    }
    private void installListeners(){
        canvas.sceneProperty().addListener((p, oldS, newS) -> {
            assert oldS == null;
            newS.addPreLayoutPulseListener(this::redrawIfNeeded);
        });
        canvas.heightProperty().addListener((observable, oldValue, newValue) -> redrawOnNextPulse());
        canvas.widthProperty().addListener((o, oV, nV) -> redrawOnNextPulse());
        property.addListener((o, oV, nV) -> redrawOnNextPulse());
    }

}

