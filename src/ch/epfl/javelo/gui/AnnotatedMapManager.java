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

import java.awt.*;
import java.util.function.Consumer;

public final class AnnotatedMapManager {
    private static final double MouseNotCloseToRoute = Double.NaN;
    private final Graph graph;
    private final TileManager tileManager;
    private final RouteBean routeBean;
    private final Consumer<String> consumer;
    private final Pane pane;
    private final WaypointsManager waypointsManager;
    private final BaseMapManager baseMapManager;
    private final RouteManager routeManager;
    private final ObjectProperty<MapViewParameters> mapViewParametersP;
    private final ObjectProperty<Point2D> mouseOnLastEvent;
    private DoubleProperty positionAlongRoute;

    public AnnotatedMapManager(Graph graph, TileManager tileManager, RouteBean routeBean, Consumer<String> consumer) {
        //todo ask for the mapViewParameters (mais je pense c bon)
        this.graph = graph;
        this.tileManager = tileManager;
        this.routeBean = routeBean;
        this.consumer = consumer;
        mouseOnLastEvent=new SimpleObjectProperty<>();
        //TODO nommage de constantes
        MapViewParameters mapViewParameters = new MapViewParameters(12, 543200, 370650);
        mapViewParametersP = new SimpleObjectProperty<>(mapViewParameters);
        waypointsManager = new WaypointsManager(graph, mapViewParametersP,routeBean.getWaypoints(), consumer);
        baseMapManager = new BaseMapManager(tileManager, waypointsManager, mapViewParametersP);
        routeManager = new RouteManager(routeBean,mapViewParametersP , consumer);
        positionAlongRoute = new SimpleDoubleProperty();
        pane = new StackPane();
        createPane();
        setEvents();
    }

    public Pane pane(){return pane;}

    public DoubleProperty mousePositionOnRouteProperty(){
        return positionAlongRoute;
    }

    private void createPane(){
        pane.getStylesheets().add("map.css");
        pane.getChildren().addAll(baseMapManager.pane(), routeManager.pane(), waypointsManager.pane());
    }

    private void setEvents(){
        pane.setOnMouseMoved(event-> {
            mouseOnLastEvent.set(new Point2D(event.getX() , event.getY()));
        });

        pane.setOnMouseExited(event-> {
            mouseOnLastEvent.set(null);
        });

        positionAlongRoute.bind(Bindings.createDoubleBinding(() -> {
            //todo essayer sur map si route est null
            //System.out.println(updatePositionAlongRoute());
            return updatePositionAlongRoute();
        }, mouseOnLastEvent, routeBean.route(), mapViewParametersP));

    }

    private double updatePositionAlongRoute(){
        if(mapViewParametersP.get()==null || routeBean.route().get()==null || mouseOnLastEvent.get()==null)return MouseNotCloseToRoute;
        PointWebMercator pointWebMercator = mapViewParametersP.get().pointAt(mouseOnLastEvent.get().getX(), mouseOnLastEvent.get().getY());
        RoutePoint pointOnRoute = routeBean.route().get().pointClosestTo(pointWebMercator.toPointCh());
        double uX=mouseOnLastEvent.get().getX()-mapViewParametersP.get().viewX(PointWebMercator.ofPointCh(pointOnRoute.point()));
        double uY=mouseOnLastEvent.get().getY()-mapViewParametersP.get().viewY(PointWebMercator.ofPointCh(pointOnRoute.point()));
        double distanceInPixels = Math2.norm(uX, uY);
        if(distanceInPixels<15){
            //System.out.println(distanceInPixels);
            return pointOnRoute.position();}
        else return MouseNotCloseToRoute;
    }

}
