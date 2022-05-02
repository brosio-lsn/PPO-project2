package ch.epfl.javelo.gui;

import ch.epfl.javelo.routing.ElevationProfile;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.scene.shape.Path;
import javafx.scene.shape.Polygon;

public final class ElevationProfileManager {
    private final ObjectProperty<ElevationProfile> elevationProfile;
    private final DoubleProperty position;
    private final BorderPane borderPane;
    private final Pane pane;
    private final VBox vbox;
    private final Path grid;
    private final Line line;
    private final Polygon profile;
    private final Group texts;

    public ElevationProfileManager(ObjectProperty<ElevationProfile> elevationProfile, DoubleProperty position) {
        this.elevationProfile = elevationProfile;
        this.position = position;
        this.pane = new Pane();
        this.vbox = new VBox();
        this.borderPane = new BorderPane();
        this.grid = new Path();
        this.profile = new Polygon(pane.getWidth(), pane.getHeight());
        this.texts = new Group();
        this.line = new Line();
        borderPane.setBottom(vbox);
        borderPane.setCenter(pane);

        drawRectangle();
    }

    public ReadOnlyObjectProperty<ElevationProfile> elevationProfileProperty() {
        return elevationProfile;
    }

    public ReadOnlyDoubleProperty positionProperty() {
        return position;
    }

    public Pane pane() {
        return borderPane;
    }

    public ReadOnlyDoubleProperty mousePositionOnProfileProperty() {
        return null;
    }

    private void drawRectangle() {
        Insets insets = new Insets(10, 10, 20, 40);

    }
}
