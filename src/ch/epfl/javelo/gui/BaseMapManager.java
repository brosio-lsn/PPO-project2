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
        int xTopLeft = (int)property.getValue().topLeft().getX();
        int yTopLeft = (int)property.getValue().topLeft().getY();
        int zoomLevel = property.getValue().zoomLevel();
    }

    public Pane pane() throws IOException {
        GraphicsContext context = canvas.getGraphicsContext2D();
        for (int i = xTopLeft; i >= 0; i--) {
            for (int j = yTopLeft; j >= 0 ; j--) {
                if (TileManager.TileId.isValid(zoomLevel, i, j)) {
                    context.drawImage(tileManager.imageForTileAt(new TileManager.TileId(zoomLevel, i, j)), topLeft, yTopLeft);
                }
            }
        }
        return pane;
    }
    private void redrawIfNeeded() {
        if (!redrawNeeded) return;
        redrawNeeded = false;
        redrawOnNextPulse();
    }
    private void redrawOnNextPulse() {
        redrawNeeded = true;
        Platform.requestNextPulse();
    }
}
