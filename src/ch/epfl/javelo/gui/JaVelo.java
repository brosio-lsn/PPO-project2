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

    @Override
    public void start(Stage primaryStage) throws IOException {
        Graph graphJavelo = Graph.loadFrom(Path.of("javelo-data"));
        Path cacheBasePathJavelo = Path.of("osm-cache");
        String tileServerHostJavelo = "tile.openstreetmap.org";
        TileManager tileManager =
                new TileManager(cacheBasePathJavelo, tileServerHostJavelo);
        CostFunction costFunction = new CityBikeCF(graphJavelo);
        RouteBean bean = new RouteBean(new RouteComputer(graphJavelo, costFunction));
        ErrorManager errorManager = new ErrorManager();
        AnnotatedMapManager map = new AnnotatedMapManager(graphJavelo, tileManager, bean, errorManager::displayError);
        MenuItem option = new MenuItem("Exporter GFX");
        Menu filesMenu = new Menu("Fichiers", null, option);
        MenuBar bar = new MenuBar(filesMenu);
        SplitPane window = new SplitPane();
        window.setOrientation(Orientation.VERTICAL);
        ObjectProperty<ElevationProfile> profileProperty=new SimpleObjectProperty<>();
        ElevationProfileManager profileManager=new ElevationProfileManager(profileProperty, bean.highlightedPositionProperty());
        bean.route().addListener((observable, oldValue, newValue) -> {
            if(bean.route().get() != null) {
                ElevationProfile profile = ElevationProfileComputer
                        .elevationProfile(bean.route().get(), 5);
                /*ObjectProperty<ElevationProfile> profileProperty =
                        new SimpleObjectProperty<>(profile);
                ElevationProfileManager profileManager =
                        new ElevationProfileManager(profileProperty,
                                bean.highlightedPositionProperty());*/
                profileProperty.set(profile);
                SplitPane.setResizableWithParent(profileManager.pane(), true);
                bar.setDisable(false);
                option.setOnAction(event -> {
                    try {
                        GpxGenerator.writeGpx("javelo.gpx", bean.route().get(), profile);} catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                });
                window.getItems().setAll(map.pane(), profileManager.pane());
                System.out.println("vbbox " + profileManager.pane().getChildren());
                bar.setUseSystemMenuBar(true);
                //TODO problèmes si null
                bean.highlightedPosition.bind(Bindings.createDoubleBinding(() -> {
                    //todo essayer sur map si route est null
                    return Double.compare(map.mousePositionOnRouteProperty().get(), Double.NaN) ==0?profileManager.mousePositionOnProfileProperty().get() :map.mousePositionOnRouteProperty().get();
                }, profileManager.mousePositionOnProfileProperty(), map.mousePositionOnRouteProperty()));
            } else {
                bean.highlightedPositionProperty().unbind();
                window.getItems().setAll(map.pane());
                bar.setDisable(true);
            }
            //bean.highlightedPosition.bind(map.mousePositionOnRouteProperty());
        });
        window.getItems().add(map.pane());

        window.getStylesheets().addAll("map.css", "error.css");
        StackPane scene = new StackPane(window, errorManager.pane(), bar);
        StackPane.setAlignment(bar, Pos.TOP_CENTER);
        primaryStage.setMinWidth(600);
        primaryStage.setMinHeight(300);
        primaryStage.setTitle("JaVelo");
        primaryStage.setScene(new Scene(scene));
        primaryStage.show();
    }
}