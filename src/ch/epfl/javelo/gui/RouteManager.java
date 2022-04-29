package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polyline;

import java.util.function.Consumer;

// recontruite full la polyline si on chnage itineraire/zoom et sinon juste la déclaer
//comment bouger la polyline (faut faire comme les waypoitns immobiles)


public final class RouteManager {
    /**
     * bean containing the properties related to the route
     */
    private final RouteBean routeBean;

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
        setEvents();
        createPane();
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
        pane = new Pane();
        pane.setPickOnBounds(false);
        polyline.setId("route");
        circle.setId("highlight");
        circle.setRadius(5);
        //check this because route might be initially null
        if (routeBean.route().get() != null) {
            createPointsCoordinates();
            positionCircle();
        }
        pane.getChildren().add(polyline);
        pane.getChildren().add(circle);
    }

    /**
     * fills the polyline with the coordinates of the points
     * it should go through
     */
    private void createPointsCoordinates() {
        Double[] arrayWithCoordinates = new Double[routeBean.route().get().points().size() * 2];
        int i = 0;
        polyline.getPoints().clear();
        //todo demander si j itere sur la bonne chose
        //todo fix si property route is null
        for (PointCh pointCh : routeBean.route().get().points()) {
            arrayWithCoordinates[i] = mapViewParameters.get().viewX(PointWebMercator.ofPointCh(pointCh));
            ++i;
            arrayWithCoordinates[i] = mapViewParameters.get().viewY(PointWebMercator.ofPointCh(pointCh));
            ++i;
        }
        polyline.getPoints().addAll(arrayWithCoordinates);
    }

    /**
     * positions the circle based on the highlighted position on the route
     */
    private void positionCircle() {
        if(routeBean.route().get()!=null) {
            PointCh pointCh = routeBean.route().get().pointAt(routeBean.highlightedPosition());
            circle.setLayoutX(mapViewParameters.get().viewX(PointWebMercator.ofPointCh(pointCh)));
            circle.setLayoutY(mapViewParameters.get().viewY(PointWebMercator.ofPointCh(pointCh)));
        }
        else System.out.println("r is null");
    }

    /**
     * sets all the events influencing the circle and the polyline
     * (invents include the change of the map parameters, of the route and of the
     * highlighted position on the route)
     */
    private void setEvents() {
        ObjectProperty<Point2D> oldLayout = new SimpleObjectProperty<>(new Point2D(polyline.getLayoutX(),polyline.getLayoutY()));
        circle.setOnMouseClicked(event -> {
            //TODO demander si y a mieux a faire que iterer comme ca
            Point2D point2D = circle.localToParent(event.getX(), event.getY());
            int nodeId = routeBean.route().get().nodeClosestTo(routeBean.highlightedPosition());
            boolean alreadyAWayPoint = false;
            for (WayPoint wayPoint : routeBean.waypoints) {
                if (wayPoint.closestNodeId() == nodeId) {
                    errorConsumer.accept("Un point de passage est déjà présent à cet endroit !");
                    alreadyAWayPoint = true;
                    break;
                }
            }
            if (!alreadyAWayPoint) {
                PointWebMercator pointWebMercator = mapViewParameters.get().pointAt(point2D.getX(), point2D.getY());
                int indexOfSegmentOfHightlightedPosition = routeBean.route().get().indexOfSegmentAt(routeBean.highlightedPosition());
                routeBean.waypoints.add(indexOfSegmentOfHightlightedPosition+1, new WayPoint(pointWebMercator.toPointCh(), nodeId));
            }
        });

        mapViewParameters.addListener((property, previousV, newV) -> {
            if (previousV.zoomLevel() != newV.zoomLevel()) {
                createPointsCoordinates();
            }
            if (!previousV.topLeft().equals(newV.topLeft())) {
                //todo demander si on doit faire une varaiable a l exterieur pour deltaX et deltaY
                double deltaX = newV.topLeft().getX() - previousV.topLeft().getX();
                double deltaY = newV.topLeft().getY() - previousV.topLeft().getY();
                polyline.setLayoutX(polyline.getLayoutX() - deltaX);
                polyline.setLayoutY(polyline.getLayoutY()  - deltaY);
            }
            oldLayout.set(new Point2D(polyline.getLayoutX(), polyline.getLayoutY()));

            positionCircle();
        });

        routeBean.highlightedPosition.addListener((property, previousV, newV) -> positionCircle());

        routeBean.route().addListener((property, previousV, newV) -> {
            if (newV == null) {
                polyline.setVisible(false);
                circle.setVisible(false);
            } else {
                polyline.setVisible(true);
                circle.setVisible(true);
                positionCircle();
                createPointsCoordinates();
            }
        });
    }

}