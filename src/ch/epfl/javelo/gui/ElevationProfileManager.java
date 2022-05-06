package ch.epfl.javelo.gui;

import ch.epfl.javelo.routing.ElevationProfile;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.scene.shape.Path;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Text;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.scene.transform.Transform;

public final class ElevationProfileManager {
    private final ObjectProperty<ElevationProfile> elevationProfile;
    private final ObjectProperty<Transform> worldToScreen;
    private final ObjectProperty<Transform> screenToWorld;
    private final ObjectProperty<Rectangle2D> rectangle;
    private final DoubleProperty position;
    private final BorderPane borderPane;
    private final Pane pane;
    private final VBox vbox;
    private final Path grid;
    private final Line line;
    private final Polygon profile;
    private final Group texts;
    private final Text stats;
    private final DoubleProperty mousePositionOnProfileProperty;
    private static final int LEFT_PIXELS = 40;
    private static final int TOP_PIXELS = 10;
    private static final int BOTTOM_PIXELS = 20;
    private static final int RIGHT_PIXELS = 10;
    private static final double MOUSE_NOT_IN_RECTANGLE = Double.NaN;
    private final Insets insets;
    private boolean bindingsDone;

    public ElevationProfileManager(ObjectProperty<ElevationProfile> elevationProfile, DoubleProperty position) {
        this.elevationProfile = elevationProfile;
        this.position = position;
        System.out.println(position.doubleValue());
        position.addListener((p,v,n)-> System.out.println(position.doubleValue()));
        this.pane = new Pane();
        this.vbox = new VBox();
        this.borderPane = new BorderPane();
        this.grid = new Path();
        this.profile = new Polygon();
        this.texts = new Group();
        this.line = new Line();
        this.stats=new Text();
        pane.getChildren().add(profile);
        pane.getChildren().add(grid);
        pane.getChildren().add(texts);
        pane.getChildren().add(line);
        borderPane.setBottom(vbox);
        borderPane.setCenter(pane);
        worldToScreen = new SimpleObjectProperty<>();
        screenToWorld=new SimpleObjectProperty<>();
        mousePositionOnProfileProperty = new SimpleDoubleProperty();
        rectangle = new SimpleObjectProperty<>();
        insets = new Insets(TOP_PIXELS, RIGHT_PIXELS, BOTTOM_PIXELS, LEFT_PIXELS);
        bindingsDone=false;
        setLabels();
        setEvents();
    }


    public Pane pane() {
        return borderPane;
    }

    public ReadOnlyDoubleProperty mousePositionOnProfileProperty() {
        return mousePositionOnProfileProperty;
    }

    private void drawPolygone() {
            //todo demander cast étrange
            int numberOfTopPoints = (int) rectangle.get().getWidth();
            int numberOfBottomPoints = 2;
            Double[] points = new Double[(numberOfBottomPoints + numberOfTopPoints) * 2];
            //todo regarder excpetions ( diviser par 0)
            double stepLength = elevationProfile.get().length() / (numberOfTopPoints - 1);
            int j = 0;
            for (int i = 0; i < numberOfTopPoints; ++i) {
                double positionOnProfile = stepLength * (i);
                double elevationAtPositionOnProfile = elevationProfile.get().elevationAt(positionOnProfile);
                Point2D pointToAdd = worldToScreen.get().transform(positionOnProfile, elevationAtPositionOnProfile);
                points[j] = pointToAdd.getX();
                ++j;
                points[j] = pointToAdd.getY();
                ++j;
            }
            points[points.length - 4] = insets.getLeft() + rectangle.get().getWidth();
            points[points.length - 3] = insets.getTop() + rectangle.get().getHeight();
            points[points.length - 2] = insets.getLeft();
            points[points.length - 1] = insets.getTop() + rectangle.get().getHeight();

            profile.getPoints().clear();
            profile.getPoints().addAll(points);
    }

    private void setLabels(){
        borderPane.getStylesheets().add("elevation_profile.css");
        vbox.setId("profile_data");
        grid.setId("grid");
        profile.setId("profile");
    }

    private String stats(){
        //todo a voir si pb que les methodes retournent des double
        String format = "Longueur : %.1f km" +
                "     Montée : %.0f m" +
                "     Descente : %.0f m" +
                "     Altitude : de %.0f m à %.0f m";
        return String.format(format, elevationProfile.get().length(),
                elevationProfile.get().totalAscent(),
                elevationProfile.get().totalDescent(),
                elevationProfile.get().minElevation(),
                elevationProfile.get().maxElevation());
    }

    private void setEvents() {
        pane.setOnMouseMoved(event -> {if(screenToWorld.get()!=null) mousePositionOnProfileProperty.set(screenToWorld.get().transform(event.getX(), event.getY()).getX());});
        pane.setOnMouseExited(event -> mousePositionOnProfileProperty.set(MOUSE_NOT_IN_RECTANGLE));
        pane.heightProperty().addListener((property, previousV, newV) -> {
            widthAndHeightlistenerContent();
        });
        pane.widthProperty().addListener((property, previousV, newV) -> {
            widthAndHeightlistenerContent();
        });
        rectangle.addListener((property, previousV, newV)-> createTransformations());

        worldToScreen.addListener((property, previousV, newV)-> drawPolygone());
    }

    private void createTransformations () {
            Affine affine = new Affine();
            affine.prependTranslation(0, -elevationProfile.get().maxElevation());
            affine.prependScale(rectangle.get().getWidth() / elevationProfile.get().length(),
                    (rectangle.get().getHeight())/ (-elevationProfile.get().maxElevation()+elevationProfile.get().minElevation()));
            affine.prependTranslation(rectangle.get().getMinX(), rectangle.get().getMinY());
            worldToScreen.set(affine);
            try {
                screenToWorld.set(affine.createInverse());
            }catch (NonInvertibleTransformException e){
                System.out.println(e.getMessage());
            }
    }

    private void setBindings(){
            bindingsDone=true;
            System.out.println(position.doubleValue());
            line.layoutXProperty().bind(Bindings.createDoubleBinding(() ->worldToScreen.get().transform(position.doubleValue(), elevationProfile.get().elevationAt(position.doubleValue())).getX()
                    , position));
            line.startYProperty().bind(Bindings.select(rectangle, "minY"));
            line.endYProperty().bind(Bindings.select(rectangle, "maxY"));
            line.visibleProperty().bind(Bindings.greaterThan(position, 0).and(Bindings.lessThan(position,elevationProfile.get().length())));
    }

    private void widthAndHeightlistenerContent (){
        double rectangle_width = pane.getWidth() - insets.getLeft() - insets.getRight();
        double rectangle_height = pane.getHeight() - insets.getBottom() - insets.getTop();
        if(rectangle_height>0 && rectangle_height>0) {
            rectangle.set(new Rectangle2D(insets.getLeft(), insets.getTop(), rectangle_width, rectangle_height));
            if (!bindingsDone) setBindings();
        }
    }
}
