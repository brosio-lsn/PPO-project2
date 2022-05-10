package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.routing.*;
import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.geometry.VerticalDirection;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.function.Consumer;

public final class JaVelo extends Application {


    @Override
    public void start(Stage primaryStage) throws UncheckedIOException, IOException {
        Graph graphJavelo = Graph.loadFrom(Path.of("javelo-data"));
        Path cacheBasePathJavelo = Path.of("osm-cache");
        String tileServerHostJavelo = "tile.openstreetmap.org";
        TileManager tileManager =
                new TileManager(cacheBasePathJavelo, tileServerHostJavelo);
        CostFunction costFunction = new CityBikeCF(graphJavelo);
        RouteBean bean = new RouteBean(new RouteComputer(graphJavelo, costFunction));
        bean.setHighlightedPosition(1000);
        ErrorManager errorManager = new ErrorManager();
        AnnotatedMapManager map = new AnnotatedMapManager(graphJavelo, tileManager, bean, errorManager::displayError);
        MenuItem option = new MenuItem("Exporter GFX");
        Menu filesMenu = new Menu("Fichiers", null, option);
        MenuBar bar = new MenuBar(filesMenu);
        SplitPane window = new SplitPane();
        window.setOrientation(Orientation.VERTICAL);

        bean.route().addListener((observable, oldValue, newValue) -> {
            if(bean.route().get() != null) {
                System.out.println("je passe par là");
                ElevationProfile profile = ElevationProfileComputer
                        .elevationProfile(bean.route().get(), 5);
                ObjectProperty<ElevationProfile> profileProperty =
                        new SimpleObjectProperty<>(profile);
                DoubleProperty highlightProperty =
                        new SimpleDoubleProperty(0);
                ElevationProfileManager profileManager =
                        new ElevationProfileManager(profileProperty,
                                highlightProperty);
                SplitPane.setResizableWithParent(profileManager.pane(), true);
                bar.setDisable(false);
                bar.setOnMouseClicked(event -> {
                    try {
                        GpxGenerator.writeGpx("javelo.gpx", bean.route().get(), profile);
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                    window.getItems().setAll(map.pane(), profileManager.pane(), errorManager.pane());
                });
                bar.setUseSystemMenuBar(true);
            } else {
                window.getItems().setAll(map.pane(), errorManager.pane());
                bar.setDisable(true);
            }
        });

        primaryStage.setMinWidth(600);
        primaryStage.setMinHeight(300);
        primaryStage.setTitle("JaVelo");
        window.getStylesheets().addAll("map.css", "elevation_profile.css", "error.css");
        window.getItems().setAll(map.pane(), errorManager.pane());
        primaryStage.setScene(new Scene(window));
        primaryStage.show();
    }
}