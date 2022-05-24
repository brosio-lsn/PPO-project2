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
/**
 * the class for the whole application
 *
 * @author Louis ROCHE (345620)
 * @author Ambroise AIGUEPERSE (341890)
 */
public final class JaVelo extends Application {
    private AnnotatedMapManager map;

    @Override
    public void start(Stage primaryStage) throws IOException {
        Graph graphJavelo = Graph.loadFrom(Path.of("javelo-data"));
        Path cacheBasePathJavelo = Path.of("osm-cache");
        String tileServerHostJavelo = "tile.openstreetmap.org";
        TileManager tileManager =
                new TileManager(cacheBasePathJavelo, tileServerHostJavelo, "");
        CostFunction costFunction = new CityBikeCF(graphJavelo);
        RouteBean bean = new RouteBean(new RouteComputer(graphJavelo, costFunction));
        ErrorManager errorManager = new ErrorManager();
        map = new AnnotatedMapManager(graphJavelo, tileManager, bean, errorManager::displayError);
        MenuItem exporterGFX = new MenuItem("Exporter GFX");
        Menu filesMenu = new Menu("Fichiers", null, exporterGFX);
        MenuBar bar = new MenuBar(filesMenu);
        bonusOptions(bar, tileManager);
        SplitPane window = new SplitPane();
        window.setOrientation(Orientation.VERTICAL);
        ObjectProperty<ElevationProfile> profileProperty=new SimpleObjectProperty<>();
        ElevationProfileManager profileManager=new ElevationProfileManager(profileProperty, bean.highlightedPositionProperty());
        bean.route().addListener((observable, oldValue, newValue) -> {
            if(bean.route().get() != null) {
                ElevationProfile profile = ElevationProfileComputer
                        .elevationProfile(bean.route().get(), 5);
                profileProperty.set(profile);
                SplitPane.setResizableWithParent(profileManager.pane(), false);
                filesMenu.setDisable(false);
                exporterGFX.setOnAction(event -> {
                    try {
                        GpxGenerator.writeGpx("javelo.gpx", bean.route().get(), profile);} catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                });
                window.getItems().setAll(
                         map.pane(),
                        profileManager.pane()
                );
                //System.out.println("vbbox " + profileManager.pane().getChildren());
                bar.setUseSystemMenuBar(true);
                //TODO problèmes si null
                bean.highlightedPosition.bind(Bindings.createDoubleBinding(() -> {
                    //todo essayer sur map si route est null
                    return Double.compare(map.mousePositionOnRouteProperty().get(), Double.NaN) ==0?profileManager.mousePositionOnProfileProperty().get() :map.mousePositionOnRouteProperty().get();
                }, profileManager.mousePositionOnProfileProperty(), map.mousePositionOnRouteProperty()));
            } else {
                bean.highlightedPositionProperty().unbind();
                window.getItems().setAll(map.pane());
                filesMenu.setDisable(true);
            }
            //bean.highlightedPosition.bind(map.mousePositionOnRouteProperty());
        });
        window.getItems().add(map.pane());
        StackPane scene = new StackPane(window, errorManager.pane(), bar);
        StackPane.setAlignment(bar, Pos.TOP_CENTER);
        primaryStage.setMinWidth(700);
        primaryStage.setMinHeight(400);
        primaryStage.setTitle("JaVelo");
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