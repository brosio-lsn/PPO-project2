package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.routing.*;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;

public final class JaVelo extends Application {

    private static final int PANE_MIN_HEIGHT = 600;
    private static final int PANE_MIN_WIDTH = 800;
    private static final String TITLE = "JaVelo";
    private static final String FILE_NAME = "Files";
    private static final String EXPORTER_GFX = "Export GFX";
    private static final String SERVER_NAME = "tile.openstreetmap.org";
    private static final String PATH_TO_REPERTORY = "osm-cache";
    private static final String NAME_OF_DATA_FILES = "javelo-data";
    private static final String FILE_NAME_WRITTEN = "javelo.gpx";
    private static final int MAX_STEP_LENGTH = 5;

    public static void main(String[] args) {
        launch(args);
    }

    /**
     * starts the JaVelo application.
     *
     * @param primaryStage window to display the application onto.
     * @throws IOException if the path given to load from is wrong.
     */
    @Override
    public void start(Stage primaryStage) throws IOException {
        Graph graphJavelo = Graph.loadFrom(Path.of(NAME_OF_DATA_FILES));
        Path cacheBasePathJavelo = Path.of(PATH_TO_REPERTORY);
        TileManager tileManager =
                new TileManager(cacheBasePathJavelo, SERVER_NAME);
        CostFunction costFunction = new CityBikeCF(graphJavelo);
        RouteBean bean = new RouteBean(new RouteComputer(graphJavelo, costFunction));
        ErrorManager errorManager = new ErrorManager();
        AnnotatedMapManager map = new AnnotatedMapManager(graphJavelo, tileManager, bean, errorManager::displayError);

        MenuItem option = new MenuItem(EXPORTER_GFX);
        Menu filesMenu = new Menu(FILE_NAME, null, option);
        MenuBar bar = new MenuBar(filesMenu);

        SplitPane window = new SplitPane();
        window.setOrientation(Orientation.VERTICAL);

        ObjectProperty<ElevationProfile> profileProperty = new SimpleObjectProperty<>();
        ElevationProfileManager profileManager = new ElevationProfileManager(profileProperty, bean.highlightedPositionProperty());
        SplitPane.setResizableWithParent(profileManager.pane(), false);

        bean.route().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                ElevationProfile profile = ElevationProfileComputer
                        .elevationProfile(bean.route().get(), MAX_STEP_LENGTH);
                profileProperty.set(profile);
                bar.setDisable(false);
                option.setOnAction(event -> {
                    try {
                        GpxGenerator.writeGpx(FILE_NAME_WRITTEN, bean.route().get(), profile);
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                });
                window.getItems().setAll(map.pane(), profileManager.pane());
                bar.setUseSystemMenuBar(true);
                bean.highlightedPosition.bind(Bindings.createDoubleBinding(() -> map.mousePositionOnRouteProperty().get() >= 0
                        ? map.mousePositionOnRouteProperty().get()
                        : profileManager.mousePositionOnProfileProperty().get(), profileManager.mousePositionOnProfileProperty(), map.mousePositionOnRouteProperty()));
            } else {
                window.getItems().setAll(map.pane());
                bar.setDisable(true);
            }
        });

        window.getItems().add(map.pane());
        StackPane scene = new StackPane(window, errorManager.pane(), bar);
        StackPane.setAlignment(bar, Pos.TOP_CENTER);
        primaryStage.setMinWidth(PANE_MIN_WIDTH);
        primaryStage.setMinHeight(PANE_MIN_HEIGHT);
        primaryStage.setTitle(TITLE);
        primaryStage.setScene(new Scene(scene));
        primaryStage.show();
    }
}