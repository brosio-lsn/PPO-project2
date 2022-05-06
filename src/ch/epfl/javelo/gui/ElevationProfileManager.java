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
 * Manages and displays the elevation profile of the route.
 */
public final class ElevationProfileManager {
    /**
     * default font size for the text to be displayed
     */
    private static final int FONT_SIZE = 10;
    /**
     * this constant is used to convert any distance in meters to kilometers (must divide)
     */
    private static final int ROUND_TO_KILOMETERS_FACTOR = 1000;
    /**
     * offset of the elevation text, used so that the text used
     * to display the various elevation scales is not in the graph
     */
    private static final int OFFSET_ELEVATION_TEXT = 2;
    /**
     * number of bottom points of the polygon
     */
    private static final int NUMBER_OF_BOTTOM_POINTS = 2;
    /**
     * elevation profile this is supposed to display
     */
    private final ObjectProperty<ElevationProfile> elevationProfile;
    /**
     * transform used to convert distances in the real world (relative to the elevation profile)
     * to distances in the screen.
     */
    private final ObjectProperty<Transform> worldToScreen;
    /**
     * inverse of the worldToScreen transform
     */
    private final ObjectProperty<Transform> screenToWorld;
    /**
     * rectangle the grid is to be displayed on
     */
    private final ObjectProperty<Rectangle2D> rectangle;
    /**
     * position on the elevationProfile
     */
    private final DoubleProperty position;
    /**
     * BorderPane which represents the main window
     */
    private final BorderPane borderPane;
    /**
     * pane the elevationProfile will be displayed on
     */
    private final Pane pane;
    /**
     * Vbox the stats of the elevationProfile will be displayed on
     */
    private final VBox vbox;
    /**
     * Grid which indicates the units of the elevation profile.
     */
    private final Path grid;
    /**
     * Line which highlights the position the mouse is on.
     */
    private final Line line;
    /**
     * Graphic representation of the elevationProfile
     */
    private final Polygon profile;
    /**
     * Texts which represent the different units of the graph (e.g. 400, 450, 500 meters for the elevation).
     */
    private final Group texts;
    /**
     * Statistics of the elevationProfile. Displays the total ascent, descent, the maximum and the minimum elevation and
     * the length of the road
     */
    private final Text stats;
    /**
     * Represents which position on the elevationProfile the mouse is at.
     */
    private final DoubleProperty mousePositionOnProfileProperty;
    /**
     * Left offset of the rectangle
     */
    private static final int LEFT_PIXELS = 40;
    /**
     * Top offset of the rectangle
     */
    private static final int TOP_PIXELS = 10;
    /**
     * Bottom offset of the rectangle
     */
    private static final int BOTTOM_PIXELS = 20;
    /**
     * Right offset of the rectangle
     */
    private static final int RIGHT_PIXELS = 10;
    /**
     * Represents the value the mouseOnProfileProperty should take when the mouse is not in the rectangle
     */
    private static final double MOUSE_NOT_IN_RECTANGLE = Double.NaN;
    /**
     * Insets representing the offsets by which the rectangle will be placed
     */
    private final Insets insets;
    /**
     * Boolean used to not initiate the bindings more than once.
     */
    private boolean bindingsDone;
    /**
     * Minimal horizontal distance between the lines of the grid
     */
    private static final int HORIZONTAL_DISTANCE_MIN = 50;
    /**
     * Minimal vertical distance between the lines of the grid
     */
    private static final int VERTICAL_DISTANCE_MIN = 25;

    /**
     * The different position steps one can use.
     */
    private static final int[] POS_STEPS =
            {1000, 2000, 5000, 10_000, 25_000, 50_000, 100_000};
    /**
     * The different elevation steps one can use.
     */
    private static final int[] ELE_STEPS =
            {5, 10, 20, 25, 50, 100, 200, 250, 500, 1_000};

    public ElevationProfileManager(ObjectProperty<ElevationProfile> elevationProfile, DoubleProperty position) {
        this.elevationProfile = elevationProfile;
        this.position = position;
        System.out.println(position.doubleValue());
        position.addListener((p, v, n) -> System.out.println(position.doubleValue()));
        this.pane = new Pane();
        this.vbox = new VBox();
        this.borderPane = new BorderPane();
        this.grid = new Path();
        this.profile = new Polygon();
        this.texts = new Group();
        this.line = new Line();
        this.stats = new Text();
        pane.getChildren().add(profile);
        pane.getChildren().add(grid);
        pane.getChildren().add(texts);
        pane.getChildren().add(line);
        vbox.getChildren().add(stats);
        borderPane.setBottom(vbox);
        borderPane.setCenter(pane);
        worldToScreen = new SimpleObjectProperty<>();
        screenToWorld = new SimpleObjectProperty<>();
        mousePositionOnProfileProperty = new SimpleDoubleProperty();
        rectangle = new SimpleObjectProperty<>();
        insets = new Insets(TOP_PIXELS, RIGHT_PIXELS, BOTTOM_PIXELS, LEFT_PIXELS);
        bindingsDone = false;
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

        int numberOfTopPoints = (int) rectangle.get().getWidth();
        Double[] points = new Double[(NUMBER_OF_BOTTOM_POINTS + numberOfTopPoints) * 2];
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

    private void setLabels() {
        borderPane.getStylesheets().add("elevation_profile.css");
        vbox.setId("profile_data");
        grid.setId("grid");
        profile.setId("profile");
    }

    private String stats() {
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
        pane.setOnMouseMoved(event -> {
            if (screenToWorld.get() != null)
                mousePositionOnProfileProperty.set(screenToWorld.get().transform(event.getX(), event.getY()).getX());
        });
        pane.setOnMouseExited(event -> mousePositionOnProfileProperty.set(MOUSE_NOT_IN_RECTANGLE));
        pane.heightProperty().addListener((property, previousV, newV) -> {
            widthAndHeightlistenerContent();
            createGrid();
        });
        pane.widthProperty().addListener((property, previousV, newV) -> {
            widthAndHeightlistenerContent();
            createGrid();
        });
        rectangle.addListener((property, previousV, newV) -> createTransformations());

        worldToScreen.addListener((property, previousV, newV) -> drawPolygone());
    }

    private void createTransformations() {
        Affine affine = new Affine();
        affine.prependTranslation(0, -elevationProfile.get().maxElevation());
        affine.prependScale(rectangle.get().getWidth() / elevationProfile.get().length(),
                (rectangle.get().getHeight()) / (-elevationProfile.get().maxElevation() + elevationProfile.get().minElevation()));
        affine.prependTranslation(rectangle.get().getMinX(), rectangle.get().getMinY());
        worldToScreen.set(affine);
        try {
            screenToWorld.set(affine.createInverse());
        } catch (NonInvertibleTransformException e) {
            System.out.println(e.getMessage());
        }
    }

    private void setBindings() {
        bindingsDone = true;
        System.out.println(position.doubleValue());
        line.layoutXProperty().bind(Bindings.createDoubleBinding(() -> worldToScreen.get().transform(position.doubleValue(), elevationProfile.get().elevationAt(position.doubleValue())).getX()
                , position));
        line.startYProperty().bind(Bindings.select(rectangle, "minY"));
        line.endYProperty().bind(Bindings.select(rectangle, "maxY"));
        line.visibleProperty().bind(Bindings.greaterThan(position, 0).and(Bindings.lessThan(position, elevationProfile.get().length())));
    }

    private void widthAndHeightlistenerContent() {
        double rectangle_width = pane.getWidth() - insets.getLeft() - insets.getRight();
        double rectangle_height = pane.getHeight() - insets.getBottom() - insets.getTop();
        if (rectangle_height > 0 && rectangle_width > 0) {
            rectangle.set(new Rectangle2D(insets.getLeft(), insets.getTop(), rectangle_width, rectangle_height));
            if (!bindingsDone) setBindings();
        }
    }
    //TODO ABSOLUMENT MODULARISER CA OMG PTDRR

    /**
     * Creates the grid of the rectangle and draws the different labels
     * representing the units of the elevation and the position.
     * The method first determines which step to use for the vertical and horizontal lines using ele_step or pos_step.
     * Then it computes the distance between each line using deltaTransform.
     * Finally, for each line on the grid it draws, it creates a text displaying the unit of the line.
     */
    private void createGrid() {
        if (rectangle.get() != null) {
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
                    break;
                }
            }
            if (stepInScreenPosition == Integer.MAX_VALUE) {
                stepInWorldPosition = POS_STEPS[POS_STEPS.length - 1];
                stepInScreenPosition = worldToScreen.get().deltaTransform(stepInWorldPosition, 0).getX();
            }
            List<Text> labels = new ArrayList<>();
            List<PathElement> positionLines = new ArrayList<>();
            double heightOfLine = worldToScreen.get().deltaTransform(0, -heightWorld).getY();
            for (int i = 0; i <= nbOfVertiLines; i++) {
                positionLines.add(new MoveTo(LEFT_PIXELS + stepInScreenPosition * i, heightOfRectangle + TOP_PIXELS));
                positionLines.add(new LineTo(LEFT_PIXELS + stepInScreenPosition * i, heightOfRectangle - heightOfLine + TOP_PIXELS));
                //TODO nommage de constantes
                Text label = new Text(String.valueOf((stepInWorldPosition * i) / ROUND_TO_KILOMETERS_FACTOR));
                label.textOriginProperty().set(VPos.TOP);
                label.relocate(LEFT_PIXELS + stepInScreenPosition * i - label.getLayoutBounds().getWidth()/2, rectangle.get().getHeight() + TOP_PIXELS);
                label.prefWidth(0);
                label.setFont(new Font("Avenir", FONT_SIZE));
                labels.add(label);
            }
            int nbOfHoriLines = 0;
            int stepInWorldElevation = 0;
            double stepInScreenElevation = Integer.MAX_VALUE;
            for (int eleStep : ELE_STEPS) {
                nbOfHoriLines = Math2.ceilDiv((int) heightWorld, eleStep);
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
            int closestStepToMinHeight = Math2.ceilDiv((int) minElevation, stepInWorldElevation) * stepInWorldElevation;
            double delta = -worldToScreen.get().deltaTransform(0, (Math2.ceilDiv((int) minElevation, stepInWorldElevation) * stepInWorldElevation - minElevation)).getY();
            List<PathElement> elevationLines = new ArrayList<>();
            for (int i = 0; i < nbOfHoriLines; i++) {
                double yCoordinateOfLine = rectangle.get().getHeight() - stepInScreenElevation * i - delta + TOP_PIXELS;
                if (!(yCoordinateOfLine < heightOfRectangle - heightOfLine)) {
                    elevationLines.add(new MoveTo(LEFT_PIXELS, yCoordinateOfLine));
                    elevationLines.add(new LineTo(widthOfRectangle + LEFT_PIXELS, yCoordinateOfLine));
                    Text label = new Text(String.valueOf((stepInWorldElevation * i + closestStepToMinHeight)));
                    label.textOriginProperty().set(VPos.CENTER);
                    label.prefWidth(0);
                    label.setFont(new Font("Avenir", FONT_SIZE));
                    //TODO nommage de constantes
                    //we substract by label.getlayoutBounds.getHeight() divided by two to recenter the text at the right height
                    label.relocate(label.getLayoutBounds().getWidth() + OFFSET_ELEVATION_TEXT, yCoordinateOfLine - label.getLayoutBounds().getHeight() / 2);
                    labels.add(label);
                }
            }
            Text unitPosition = new Text("km");
            Text unitElevation = new Text("m");

            unitPosition.relocate(rectangle.get().getWidth()+LEFT_PIXELS, rectangle.get().getHeight()+TOP_PIXELS);
            unitPosition.setFont(new Font("Avenir", FONT_SIZE-1));
            labels.add(unitPosition);
            positionLines.addAll(elevationLines);
            unitElevation.relocate(0, 0);
            unitElevation.setFont(new Font("Avenir", FONT_SIZE-1));
            labels.add(unitElevation);
            texts.getChildren().setAll(labels);
            grid.getElements().setAll(positionLines);
        }
    }
}
