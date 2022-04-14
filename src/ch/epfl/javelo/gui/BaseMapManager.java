package ch.epfl.javelo.gui;

import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;

import java.io.IOException;


public final class BaseMapManager {
    private final TileManager tileManager;
    private final WaypointsManager waypointsManager;
    private final Property<MapViewParameters> property;
    private final Canvas canvas;
    private final Pane pane;
    boolean redrawNeeded;
    private int xTopLeft;
    private int yTopLeft;
    private int zoomLevel;
    private final static int POINTS_PER_TILE = 256;

    public BaseMapManager(TileManager tileManager, WaypointsManager waypointsManager, Property<MapViewParameters> property) {
        this.waypointsManager = waypointsManager;
        this.property = property;
        this.tileManager = tileManager;
        canvas = new Canvas();
        pane = new Pane(canvas);
        canvas.widthProperty().bind(pane.widthProperty());
        canvas.heightProperty().bind(pane.heightProperty());
        canvas.sceneProperty().addListener((p, oldS, newS) -> {
            assert oldS == null;
            newS.addPreLayoutPulseListener(this::redrawIfNeeded);
        });
    }

    public Pane pane() throws IOException {
        GraphicsContext context = canvas.getGraphicsContext2D();
        for (int x = xTopLeft / POINTS_PER_TILE; x >= 0; x--) {
            for (int y = yTopLeft / POINTS_PER_TILE; y >= 0; y--) {
                context.drawImage(tileManager.imageForTileAt(new TileManager.TileId(zoomLevel, x, y)), x, y);
            }
        }
        return pane;
    }

    private void redrawIfNeeded() {
        if (!redrawNeeded) return;
        redrawNeeded = false;
        xTopLeft = (int) property.getValue().topLeft().getX();
        yTopLeft = (int) property.getValue().topLeft().getY();
        zoomLevel = property.getValue().zoomLevel();
        redrawOnNextPulse();
    }

    private void redrawOnNextPulse() {
        redrawNeeded = true;
        Platform.requestNextPulse();
    }
}
