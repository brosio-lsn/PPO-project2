package ch.epfl.javelo.gui;

import ch.epfl.javelo.gui.TileManager;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class TileManagerTest extends Application {
    public static void main(String[] args) { launch(args); }

    @Override
    public void start(Stage primaryStage) throws IOException {
        TileManager tm = new TileManager(
                Path.of("."), "tile.openstreetmap.org");
        Image tileImage = tm.imageForTileAt(
                new TileManager.TileId(19, 271725, 185422));
        Platform.exit();
    }
    @Disabled
    @Test
    void isValidWorks() {
        assertFalse(TileManager.TileId.isValid(0, 2, 1));
        assertFalse(TileManager.TileId.isValid(0, -1, 1));
    }
}