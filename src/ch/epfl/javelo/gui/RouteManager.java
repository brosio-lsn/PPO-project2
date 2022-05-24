package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import ch.epfl.javelo.routing.Route;
import com.sun.jdi.PrimitiveValue;
import com.sun.security.jgss.GSSUtil;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polyline;

import java.rmi.server.ExportException;
import java.util.function.Consumer;

// recontruite full la polyline si on chnage itineraire/zoom et sinon juste la déclaer
//comment bouger la polyline (faut faire comme les waypoitns immobiles)


public final class RouteManager {

    /**
     * error message
     */
    private static final String ERROR_MESSAGE = "Un point de passage est déjà présent à cet endroit !";

    /**
     * y center coordinate
     */
    private static final int Y_CENTER = 0;

    /**
     * x center coordinate
     */
    private static final int X_CENTER = 0;
    private static final int CIRCLE_RADIUS = 5;

    /**
     * bean containing the properties related to the route
     */
    private final RouteBean routeBean;

    /**
     * constant to signal that the circle shouldn't be drawn
     */
    private static final double noPosition = Double.NaN;

    /**
     * property containing the parameters of the displayed map
     */
    private final ObjectProperty<MapViewParameters> mapViewParameters;

    /**
     * error consumer to display an error
     */
    private final Consumer<String> errorConsumer;

    /**
     * circle representing the highlighted position on the route
     */
    private Circle circle;

    /**
     * represents the route itself on the map
     */
    private Polyline polyline;

    /**
     * the pane containing the route (polyline and the circle)
     */
    private Pane pane;

    /**
     * constructor of the class
     *
     * @param routeBean         bean containing the properties related to the route
     * @param mapViewParameters property containing the parameters of the displayed map
     * @param errorConsumer     circle representing the highlighted position on the route
     */
    public RouteManager(RouteBean routeBean, ObjectProperty<MapViewParameters> mapViewParameters, Consumer<String> errorConsumer) {
        this.routeBean = routeBean;
        this.mapViewParameters = mapViewParameters;
        this.errorConsumer = errorConsumer;
        polyline = new Polyline();
        circle = new Circle();
        createPane();
        setEvents();
    }

    /**
     * returns the pane containing the route (polyline and the circle)
     *
     * @return the pane containing the route (polyline and the circle)
     */
    public Pane pane() {
        return pane;
    }

    /**
     * creates the pane (used in the constructor) and sets
     * the circle and te polyline as its children
     */
    private void createPane() {
        //todo demander si constantes pour ces strings
        polyline.setId("route");
        circle.setId("highlight");
        circle.setRadius(CIRCLE_RADIUS);
        pane = new Pane(polyline, circle);
        pane.setPickOnBounds(false);
        createPointsCoordinates();
        positionCircle();
    }

    /**
     * fills the polyline with the coordinates of the points
     * it should go through
     */
    private void createPointsCoordinates() {
        Route route = routeBean.route().get();
        if (route != null) {
            polyline.setLayoutX(X_CENTER);
            polyline.setLayoutY(Y_CENTER);
            Double[] arrayWithCoordinates = new Double[route.points().size() * 2];
            //didn't use a for i loop for more flexibility (in case the list implementation of list changed,
            //with the iterator the complexity wouldn't be increased)
            int i = 0;
            for (PointCh pointCh : route.points()) {
                PointWebMercator pointWebMercator = PointWebMercator.ofPointCh(pointCh);
                arrayWithCoordinates[i] = mapViewParameters.get().viewX(pointWebMercator);
                ++i;
                arrayWithCoordinates[i] = mapViewParameters.get().viewY(pointWebMercator);
                ++i;
            }
            polyline.getPoints().setAll(arrayWithCoordinates);
            polyline.setVisible(true);
        } else polyline.setVisible(false);
    }

    /**
     * positions the circle based on the highlighted position on the route
     */
    private void positionCircle() {
        if (routeBean.route().get() != null && !Double.isNaN(routeBean.highlightedPosition())) {
            PointCh pointCh = routeBean.route().get().pointAt(routeBean.highlightedPosition());
            PointWebMercator pointWebMercator = PointWebMercator.ofPointCh(pointCh);
            circle.setLayoutX(mapViewParameters.get().viewX(pointWebMercator));
            circle.setLayoutY(mapViewParameters.get().viewY(pointWebMercator));
            circle.setVisible(true);
        } else circle.setVisible(false);
    }

    /**
     * sets all the events influencing the circle and the polyline
     * (invents include the change of the map parameters, of the route and of the
     * highlighted position on the route)
     */
    private void setEvents() {
        //todo demander si moy d opti la methode
        circle.setOnMouseClicked(event -> {
            Point2D point2D = circle.localToParent(event.getX(), event.getY());
            int nodeId = routeBean.route().get().nodeClosestTo(routeBean.highlightedPosition());

            PointWebMercator pointWebMercator = mapViewParameters.get().pointAt(point2D.getX(), point2D.getY());
            int indexOfSegmentAtHightlightedPosition = routeBean.indexOfNonEmptySegmentAt(routeBean.highlightedPosition());
            routeBean.getWaypoints().add(indexOfSegmentAtHightlightedPosition + 1, new WayPoint(pointWebMercator.toPointCh(), nodeId));
        });

        mapViewParameters.addListener((property, previousV, newV) -> {
            if (previousV.zoomLevel() != newV.zoomLevel())
                createPointsCoordinates();
            else {
                double deltaX = newV.topLeft().getX() - previousV.topLeft().getX();
                double deltaY = newV.topLeft().getY() - previousV.topLeft().getY();
                polyline.setLayoutX(polyline.getLayoutX() - deltaX);
                polyline.setLayoutY(polyline.getLayoutY() - deltaY);
            }
            positionCircle();
        });

        routeBean.highlightedPosition.addListener((property, previousV, newV) -> positionCircle());

        routeBean.route().addListener((property, previousV, newV) -> {
            positionCircle();
            createPointsCoordinates();
        });
    }

}