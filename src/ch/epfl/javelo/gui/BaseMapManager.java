package ch.epfl.javelo.gui;

import ch.epfl.javelo.Math2;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
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


public final class BaseMapManager {
    private final TileManager tileManager;
    private final WaypointsManager waypointsManager;
    private final ObjectProperty<MapViewParameters> property;
    private final Canvas canvas;
    private final Pane pane;
    boolean redrawNeeded;
    EventHandler<MouseEvent> mouseDrag, mouseClick;
    EventHandler<ScrollEvent> mouseScroll;
    private final static int PIXELS_PER_TILE = 256;
    private int xTopLeft, yTopLeft, zoomLevel;

    public BaseMapManager(TileManager tileManager, WaypointsManager waypointsManager, ObjectProperty<MapViewParameters> property) {
        this.waypointsManager = waypointsManager;
        this.property = property;
        this.tileManager = tileManager;
        canvas = new Canvas();
        pane = new Pane(canvas);
        canvas.sceneProperty().addListener((p, oldS, newS) -> {
            assert oldS == null;
            newS.addPreLayoutPulseListener(this::redrawIfNeeded);
        });
        redrawOnNextPulse();
        canvas.widthProperty().bind(pane.widthProperty());
        //  canvas.widthProperty().addListener(property.addListener(redrawOnNextPulse());
        //});
        canvas.heightProperty().bind(pane.heightProperty());
        createHandlers();
        canvas.heightProperty().addListener((observable, oldValue, newValue) -> redrawOnNextPulse());
        canvas.widthProperty().addListener((o, oV, nV) -> redrawOnNextPulse());
    }

    public Pane pane() {
        return pane;
    }

    private void redrawIfNeeded() {
        xTopLeft = (int) property.getValue().topLeft().getX();
        yTopLeft = (int) property.getValue().topLeft().getY();
        zoomLevel = property.getValue().zoomLevel();
        if (!redrawNeeded) return;
        redrawNeeded = false;
        GraphicsContext context = canvas.getGraphicsContext2D();
        Image imageToDraw;
        boolean canDraw = true;
        for (int x = 0; x <= canvas.getWidth() + PIXELS_PER_TILE; x += PIXELS_PER_TILE) {
            for (int y = 0; y <= canvas.getHeight() + PIXELS_PER_TILE; y += PIXELS_PER_TILE) {
                int yTileIndex = (yTopLeft + y) / PIXELS_PER_TILE;
                int xTileIndex = (xTopLeft + x) / PIXELS_PER_TILE;
                try {
                    imageToDraw = tileManager.imageForTileAt(new TileManager.TileId(zoomLevel, xTileIndex, yTileIndex));
                } catch (IOException e) {
                    canDraw = false;
                    break;
                }
                if (canDraw) {
                    System.out.println(xTileIndex + "-" + yTileIndex);
                    context.drawImage(imageToDraw, xTileIndex * PIXELS_PER_TILE - xTopLeft, yTileIndex * PIXELS_PER_TILE - yTopLeft);
                }
                canDraw = true;
            }
        }
    }

    private void redrawOnNextPulse() {
        redrawNeeded = true;
        Platform.requestNextPulse();
    }

    private void createHandlers() {
        mouseScroll = new EventHandler<>() {
            @Override
            public void handle(ScrollEvent event) {
                pane.setOnScroll(this);
                zoomLevel = (int) Math2.clamp(8, zoomLevel + event.getDeltaY(), 19);

            }
        };
        mouseDrag = new EventHandler<>() {
            @Override
            public void handle(MouseEvent event) {
                pane.setOnMouseDragged(this);
                pane.setOnMouseReleased(this);
                SimpleObjectProperty<Point2D> mousePoint = new SimpleObjectProperty<>(new Point2D(event.getX(), event.getY()));
                if (!event.isStillSincePress()) {
                    double deltaX = event.getX() - mousePoint.get().getX();
                    double deltaY = event.getY() - mousePoint.get().getY();
                    mousePoint.get().add(deltaX, deltaY);
                    xTopLeft += deltaX;
                    yTopLeft += deltaY;
                }
            }
        };
        mouseClick = new EventHandler<>() {
            @Override
            public void handle(MouseEvent event) {
                pane.setOnMousePressed(this);
                waypointsManager.addWayPoint(event.getX(), event.getY());

            }
        };
    }
}
