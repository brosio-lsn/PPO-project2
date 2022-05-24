package ch.epfl.javelo.gui;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointWebMercator;
import ch.epfl.javelo.routing.RoutePoint;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import java.util.function.Consumer;
/**
 * @author Louis ROCHE (345620)
 * @author Ambroise AIGUEPERSE (341890)
 *
 * handels the display of the annotated map
 */
public final class AnnotatedMapManager {
    private static final double MOUSE_NOT_CLOSE_TO_ROUTE = Double.NaN;
    private static final int MAXIMAL_PIXEL_DISTANCE_FOR_MOUSE = 15;
    private static final int INITIAL_ZOOM_LEVEL = 12;
    private static final int INITIAL_X = 543200;
    private static final int INITIAL_Y = 370650;
    private final RouteBean routeBean;
    private final Pane pane;
    private final WaypointsManager waypointsManager;
    private final BaseMapManager baseMapManager;
    private final RouteManager routeManager;
    private final ObjectProperty<MapViewParameters> mapViewParametersP;
    private final ObjectProperty<Point2D> mouseOnLastEvent;
    private final DoubleProperty positionAlongRoute;

    /**
     * constructor of the class
     * @param graph the with all the edges, node...
     * @param tileManager the tile manager
     * @param routeBean the bean of the displayed route
     * @param consumer a consumer to display errors
     */
    public AnnotatedMapManager(Graph graph, TileManager tileManager, RouteBean routeBean, Consumer<String> consumer) {
        this.routeBean = routeBean;
        mouseOnLastEvent=new SimpleObjectProperty<>();
        MapViewParameters mapViewParameters = new MapViewParameters(INITIAL_ZOOM_LEVEL, INITIAL_X, INITIAL_Y);
        mapViewParametersP = new SimpleObjectProperty<>(mapViewParameters);
        waypointsManager = new WaypointsManager(graph, mapViewParametersP,routeBean.getWaypoints(), consumer);
        baseMapManager = new BaseMapManager(tileManager, waypointsManager, mapViewParametersP);
        routeManager = new RouteManager(routeBean,mapViewParametersP , consumer);
        positionAlongRoute = new SimpleDoubleProperty();
        pane = new StackPane(baseMapManager.pane(), routeManager.pane(), waypointsManager.pane());
        pane.getStylesheets().add("map.css");
        setEvents();
    }

    /**
     * returns the pane with the annotated map
     * @return the pane with the annotated map
     */
    public Pane pane(){return pane;}

    /**
     * returns the property containing the position of the mouse along the route
     * @return the property containing the position of the mouse along the route
     */
    public DoubleProperty mousePositionOnRouteProperty(){
        return positionAlongRoute;
    }

    /**
     * sets all the events for the class
     */
    private void setEvents(){
        pane.setOnMouseMoved(event-> mouseOnLastEvent.set(new Point2D(event.getX() , event.getY())));

        pane.setOnMouseExited(event-> mouseOnLastEvent.set(null));

        positionAlongRoute.bind(Bindings.createDoubleBinding(
                this::updatePositionAlongRoute, mouseOnLastEvent, routeBean.route(), mapViewParametersP));
    }

    private double updatePositionAlongRoute(){
        if(mapViewParametersP.get()==null || routeBean.route().get()==null || mouseOnLastEvent.get()==null)return MOUSE_NOT_CLOSE_TO_ROUTE;
        PointWebMercator pointWebMercator = mapViewParametersP.get().pointAt(mouseOnLastEvent.get().getX(), mouseOnLastEvent.get().getY());
        RoutePoint pointOnRoute = routeBean.route().get().pointClosestTo(pointWebMercator.toPointCh());
        PointWebMercator pointWebMercatorOfPointCh = PointWebMercator.ofPointCh(pointOnRoute.point());
        double uX=mouseOnLastEvent.get().getX()-mapViewParametersP.get().viewX(pointWebMercatorOfPointCh);
        double uY=mouseOnLastEvent.get().getY()-mapViewParametersP.get().viewY(pointWebMercatorOfPointCh);
        double distanceInPixels = Math2.norm(uX, uY);
        if(distanceInPixels< MAXIMAL_PIXEL_DISTANCE_FOR_MOUSE){
            return pointOnRoute.position();}
        else return MOUSE_NOT_CLOSE_TO_ROUTE;
    }

}