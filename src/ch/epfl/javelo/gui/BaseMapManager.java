package ch.epfl.javelo.gui;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;

import java.io.IOException;


public final class BaseMapManager {
    private final TileManager tileManager;
    private final WaypointsManager waypointsManager;
    private final ObjectProperty<MapViewParameters> property;
    private final Canvas canvas;
    private final Pane pane;
    boolean redrawNeeded;
    private int xTopLeft;
    private int yTopLeft;
    private int zoomLevel;
    private final static int POINTS_PER_TILE = 256;
   /* private final int yBottomLeft;
    private final int xBottomLeft;

    */

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
        canvas.widthProperty().bind(pane.widthProperty());
        canvas.heightProperty().bind(pane.heightProperty());
        redrawOnNextPulse();
    }

    public Pane pane() {
        return pane;
    }

    private void redrawIfNeeded() {
        if (!redrawNeeded) return;
        redrawNeeded = false;
        xTopLeft = (int) property.getValue().topLeft().getX();
        yTopLeft = (int) property.getValue().topLeft().getY();
        zoomLevel = property.getValue().zoomLevel();
        GraphicsContext context = canvas.getGraphicsContext2D();
        Image imageToDraw;
        for (int x = 0; x <= canvas.getWidth(); x++) {
            for (int y = 0; y <= canvas.getHeight(); y++) {
                try {
                    imageToDraw = tileManager.imageForTileAt(new TileManager.TileId(zoomLevel, (xTopLeft - x) / POINTS_PER_TILE,
                            (yTopLeft - y) / POINTS_PER_TILE));
                } catch (IOException e) {
                    break;
                }
                context.drawImage(imageToDraw, x, y);
            }
        }
    }

    private void redrawOnNextPulse() {
        redrawNeeded = true;
        Platform.requestNextPulse();
    }

}
