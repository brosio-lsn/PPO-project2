package ch.epfl.javelo.gui;

import ch.epfl.javelo.gui.TileManager;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class TileManagerTest extends Application {
    public static void main(String[] args) { launch(args); }

    @Override
    public void start(Stage primaryStage) throws IOException {
        TileManager tm = new TileManager(
                Path.of("C:/Users/louis/PPO-project2/out/images"), "tile.openstreetmap.org");
        Image tileImage = tm.imageForTileAt(
                new TileManager.TileId(19, 271725, 185422));
        Platform.exit();

    }
}