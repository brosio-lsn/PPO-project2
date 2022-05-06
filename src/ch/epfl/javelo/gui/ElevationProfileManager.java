package ch.epfl.javelo.gui;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.routing.ElevationProfile;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.geometry.*;
import javafx.scene.Group;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.scene.transform.Transform;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages the
 */
public final class ElevationProfileManager {
    public static final int FONT_SIZE = 10;
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
    private static final int HORIZONTAL_DISTANCE_MIN = 50;
    private static final int VERTICAL_DISTANCE_MIN = 25;
    private static final int[] POS_STEPS =
            {1000, 2000, 5000, 10_000, 25_000, 50_000, 100_000};
    private static final int[] ELE_STEPS =
            {5, 10, 20, 25, 50, 100, 200, 250, 500, 1_000};

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
        vbox.getChildren().add(stats);
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
        stats.setText(stats());
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
            createGrid();
        });
        pane.widthProperty().addListener((property, previousV, newV) -> {
            widthAndHeightlistenerContent();
            createGrid();
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
        if(rectangle_height>0 && rectangle_width>0) {
            rectangle.set(new Rectangle2D(insets.getLeft(), insets.getTop(), rectangle_width, rectangle_height));
            if (!bindingsDone) setBindings();
        }
    }
    //TODO ABSOLUMENT MODULARISER CA OMG PTDRR

    private void createGrid() {
        grid.getElements().clear();
        texts.getChildren().clear();
        //determining the horizontal steps used
        double lengthWorld = elevationProfile.get().length();
        double heightWorld = elevationProfile.get().maxElevation() - elevationProfile.get().minElevation();
        double widthOfRectangle = rectangle.get().getWidth();
        double heightOfRectangle = rectangle.get().getHeight();
        double stepInScreenPosition = Integer.MAX_VALUE;
        int stepInWorldPosition = 0;
        int nbOfVertiLines = 0;
        for (int posStep : POS_STEPS) {
            nbOfVertiLines = (int) (lengthWorld / posStep);
            double distanceBetweenLines = worldToScreen.get().deltaTransform(posStep, 0).getX();
            if (distanceBetweenLines >= HORIZONTAL_DISTANCE_MIN) {
                stepInWorldPosition = posStep;
                stepInScreenPosition = distanceBetweenLines;
                System.out.println(stepInScreenPosition);
                break;
            }
        }
        if (stepInScreenPosition == Integer.MAX_VALUE) {
            stepInWorldPosition = POS_STEPS[POS_STEPS.length - 1];
            stepInScreenPosition = worldToScreen.get().deltaTransform(stepInWorldPosition, 0).getX();
        }
        List<PathElement> positionLines = new ArrayList<>();
        double heightOfLine = worldToScreen.get().deltaTransform(0, -heightWorld).getY();
        for (int i = 0; i <= nbOfVertiLines; i++) {
            positionLines.add(new MoveTo(LEFT_PIXELS + stepInScreenPosition * i, heightOfRectangle+TOP_PIXELS));
            positionLines.add(new LineTo(LEFT_PIXELS + stepInScreenPosition * i, heightOfRectangle - heightOfLine+TOP_PIXELS));
            //TODO nommage de constantes
            Text label = new Text(String.valueOf((int)(stepInWorldPosition * i)/1000));
            label.textOriginProperty().set(VPos.TOP);
            label.relocate(LEFT_PIXELS + stepInScreenPosition*i, rectangle.get().getHeight()+TOP_PIXELS);
            label.prefWidth(0);
            label.setFont(new Font("Avenir", FONT_SIZE));
            texts.getChildren().add(label);
        }
        grid.getElements().addAll(positionLines);
        int nbOfHoriLines = 0;
        int stepInWorldElevation = 0;
        double stepInScreenElevation = Integer.MAX_VALUE;
        for (int eleStep : ELE_STEPS) {
            nbOfHoriLines = Math2.ceilDiv((int)heightWorld, eleStep);
            double distance = worldToScreen.get().deltaTransform(0, -eleStep).getY();
            if (distance >= VERTICAL_DISTANCE_MIN) {
                stepInWorldElevation = eleStep;
                stepInScreenElevation = distance;
                break;
            }
        }
        if (stepInScreenElevation == Integer.MAX_VALUE) {
            stepInWorldElevation = POS_STEPS[POS_STEPS.length - 1];
            stepInScreenElevation = worldToScreen.get().deltaTransform(0, -stepInWorldElevation).getY();
        }
        double minElevation = elevationProfile.get().minElevation();
        int closestStepToMinHeight = Math2.ceilDiv((int)minElevation, stepInWorldElevation) * stepInWorldElevation;
        double delta = -worldToScreen.get().deltaTransform(0, (Math2.ceilDiv( (int)minElevation,  stepInWorldElevation) * stepInWorldElevation - minElevation)).getY();
        List<PathElement> elevationLines = new ArrayList<>();
        for (int i = 0; i < nbOfHoriLines; i++) {
            double yCoordinateOfLine = rectangle.get().getHeight() - stepInScreenElevation * i - delta+TOP_PIXELS;
            if (!(yCoordinateOfLine < heightOfRectangle - heightOfLine)) {
                elevationLines.add(new MoveTo(LEFT_PIXELS, yCoordinateOfLine));
                elevationLines.add(new LineTo(widthOfRectangle + LEFT_PIXELS, yCoordinateOfLine));
                Text label = new Text(String.valueOf((int)(stepInWorldElevation * i + closestStepToMinHeight)));
                label.textOriginProperty().set(VPos.CENTER);
                label.prefWidth(0);
                label.setFont(new Font("Avenir", 10));
                //TODO nommage de constantes
                label.relocate(label.getLayoutBounds().getWidth()+2, yCoordinateOfLine-label.getLayoutBounds().getHeight()/2);
                texts.getChildren().add(label);
            }
        }
        grid.getElements().addAll(elevationLines);
    }
}
