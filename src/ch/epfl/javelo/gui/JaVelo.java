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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
/**
 * @author Louis ROCHE (345620)
 * @author Ambroise AIGUEPERSE (341890)
 *
 * final application
 */
public final class JaVelo extends Application {

    private static final int PANE_MIN_HEIGHT = 600;
    private static final int PANE_MIN_WIDTH = 800;
    private static final String TITLE = "JaVelo";
    private static final String FILE_NAME = "Fichiers";
    private static final String EXPORTER_GFX = "Exporter GFX";
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
        bonusOptions(bar, tileManager);

        SplitPane window = new SplitPane();
        window.setOrientation(Orientation.VERTICAL);
        window.getItems().add(map.pane());

        ObjectProperty<ElevationProfile> profileProperty = new SimpleObjectProperty<>();
        ElevationProfileManager profileManager = new ElevationProfileManager(profileProperty, bean.highlightedPositionProperty());
        SplitPane.setResizableWithParent(profileManager.pane(), false);

        bean.route().addListener((observable, oldValue, newValue) -> {
            window.getItems().remove(profileManager.pane());
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
                window.getItems().add(profileManager.pane());
                bar.setUseSystemMenuBar(true);
                bean.highlightedPositionProperty().bind(Bindings.createDoubleBinding(() -> map.mousePositionOnRouteProperty().get() >= 0
                        ? map.mousePositionOnRouteProperty().get()
                        : profileManager.mousePositionOnProfileProperty().get(), profileManager.mousePositionOnProfileProperty(), map.mousePositionOnRouteProperty()));
            } else {
                bar.setDisable(true);
            }
        });


        StackPane mapView = new StackPane(window, errorManager.pane());
        BorderPane scene = new BorderPane(mapView, bar, null, null, null);
        primaryStage.setMinWidth(PANE_MIN_WIDTH);
        primaryStage.setMinHeight(PANE_MIN_HEIGHT);
        primaryStage.setTitle(TITLE);
        primaryStage.setScene(new Scene(scene));
        primaryStage.show();
    }
    private void bonusOptions(MenuBar bar, TileManager tileManager) {
        MenuItem cycleOSM = new MenuItem("Route adaptée aux vélos");
        MenuItem defaultOSM = new MenuItem("Route par défaut");
        MenuItem landscapeOSM = new MenuItem("Carte Paysage");
        MenuItem realistOSM = new MenuItem("Carte réaliste");
        MenuItem swissOSM = new MenuItem("Carte spéciale Suisse");
        Menu itineraryOptions = new Menu("Affichage de la carte", null, defaultOSM, cycleOSM, landscapeOSM, realistOSM, swissOSM);
        cycleOSM.setOnAction(e -> tileManager.setNewServer("tile.thunderforest.com/cycle", "?apikey=00364017c0f944099888ffa7a7e24159"));
        defaultOSM.setOnAction(e -> {
            tileManager.setNewServer("tile.openstreetmap.org", "");

        });
        landscapeOSM.setOnAction(e -> tileManager.setNewServer("tile.thunderforest.com/landscape", "?apikey=00364017c0f944099888ffa7a7e24159"));
        realistOSM.setOnAction(e -> tileManager.setNewServer("tile.thunderforest.com/outdoors", "?apikey=00364017c0f944099888ffa7a7e24159"));
        swissOSM.setOnAction(e -> tileManager.setNewServer("tile.osm.ch/osm-swiss-style/", ""));
        bar.getMenus().add(itineraryOptions);
    }
}