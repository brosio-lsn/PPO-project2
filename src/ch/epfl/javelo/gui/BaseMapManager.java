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
    private final static int PIXELS_PER_TILE = 256;

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
        canvas.heightProperty().bind(pane.heightProperty());
        double a = canvas.getWidth();
    }

    public Pane pane() {
        return pane;
    }

    private void redrawIfNeeded() {
        if (!redrawNeeded) return;
        redrawNeeded = false;
        int xTopLeft = (int) property.getValue().topLeft().getX();
        int yTopLeft = (int) property.getValue().topLeft().getY();
        int zoomLevel = property.getValue().zoomLevel();
        GraphicsContext context = canvas.getGraphicsContext2D();
        Image imageToDraw;
        boolean canDraw = true;
        for (int x = 0; x <= canvas.getWidth()+PIXELS_PER_TILE; x += PIXELS_PER_TILE) {
            for (int y = 0; y <= canvas.getHeight()+PIXELS_PER_TILE; y += PIXELS_PER_TILE) {
                int yTileIndex = (yTopLeft+y)/ PIXELS_PER_TILE;
                int xTileIndex = (xTopLeft+x)/ PIXELS_PER_TILE;
                try {
                    imageToDraw = tileManager.imageForTileAt(new TileManager.TileId(zoomLevel, xTileIndex, yTileIndex));
                } catch (IOException e) {
                    canDraw = false;
                    break;
                }
                if (canDraw) {
                    System.out.println(xTileIndex + "-" + yTileIndex);
                    context.drawImage(imageToDraw, xTileIndex*PIXELS_PER_TILE - xTopLeft, yTileIndex*PIXELS_PER_TILE-yTopLeft);
                }
                canDraw = true;
            }
        }
        redrawOnNextPulse();
    }

    private void redrawOnNextPulse() {
        redrawNeeded = true;
        Platform.requestNextPulse();
    }

}
