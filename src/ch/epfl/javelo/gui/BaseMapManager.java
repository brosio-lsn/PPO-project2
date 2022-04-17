package ch.epfl.javelo.gui;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.projection.PointWebMercator;
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
    private final static int POINTS_PER_TILE = 256;

    public BaseMapManager(TileManager tileManager, WaypointsManager waypointsManager, ObjectProperty<MapViewParameters> property) {
        this.waypointsManager = waypointsManager;
        this.property = property;
        this.tileManager = tileManager;
        canvas = new Canvas(600, 300);
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
        int xTopLeft = (int) property.getValue().topLeft().getX();
        int yTopLeft = (int) property.getValue().topLeft().getY();
        int zoomLevel = property.getValue().zoomLevel();
        GraphicsContext context = canvas.getGraphicsContext2D();
        Image imageToDraw;
        boolean canDraw = true;
        System.out.println(canvas.getWidth());
        System.out.println(canvas.getHeight());
        for (int x = 0; x <= canvas.getWidth(); x+= POINTS_PER_TILE) {
            for (int y = 0; y <= canvas.getHeight(); y+= POINTS_PER_TILE) {
                int yTileIndex = Math2.ceilDiv(yTopLeft+y, POINTS_PER_TILE);
                int xTileIndex = Math2.ceilDiv(xTopLeft+x, POINTS_PER_TILE);
                try {
                    imageToDraw = tileManager.imageForTileAt(new TileManager.TileId(zoomLevel, xTileIndex,
                            (yTileIndex)));
                } catch (IOException e) {
                    canDraw = false;
                    break;
                }
                if (canDraw) context.drawImage(imageToDraw, Math2.clamp(0, x, canvas.getWidth()), Math2.clamp(0,y,canvas.getHeight()));
                canDraw = true;
            }
        }
    }

    private void redrawOnNextPulse() {
        redrawNeeded = true;
        Platform.requestNextPulse();
    }

}
