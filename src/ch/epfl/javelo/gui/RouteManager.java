package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import javafx.beans.property.ObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polyline;

import java.util.function.Consumer;

// recontruite full la polyline si on chnage itineraire/zoom et sinon juste la déclaer
//comment bouger la polyline (faut faire comme les waypoitns immobiles)


public final class RouteManager {

    private final RouteBean routeBean;

    private final ObjectProperty<MapViewParameters> mapViewParameters;

    private final Consumer<String> errorConsumer;

    private Circle circle;

    private Polyline polyline;

    private Pane pane;


    public RouteManager(RouteBean routeBean, ObjectProperty<MapViewParameters> mapViewParameters, Consumer<String> errorConsumer){
        this.routeBean=routeBean;
        this.mapViewParameters=mapViewParameters;
        this.errorConsumer=errorConsumer;
        createPane();
        polyline = new Polyline();
        circle = new Circle();
        setEvents();
    }

    public Pane pane(){return pane;}

    private void createPane(){
        pane = new Pane();
        pane.setPickOnBounds(false);
        polyline.setId("route");
        circle.setId("highlight");
        circle.setRadius(5);
        //check this because route might be initially null
        if(routeBean.route().get()!=null) {
            createPointsCoordinates();
            positionateCircle();
        }
        pane.getChildren().add(polyline);
        pane.getChildren().add(circle);
    }

    private void createPointsCoordinates(){
        Double [] arrayWithCoordinates = new Double[routeBean.route().get().points().size()*2];
        int i=0;
        for(PointCh pointCh : routeBean.route().get().points()){
            arrayWithCoordinates[i]= mapViewParameters.get().viewX(PointWebMercator.ofPointCh(pointCh));
            ++i;
            arrayWithCoordinates[i]= mapViewParameters.get().viewY(PointWebMercator.ofPointCh(pointCh));
            ++i;
        }
        polyline.getPoints().addAll(arrayWithCoordinates);
    }

    private void positionateCircle(){
        PointCh pointCh= routeBean.route().get().pointAt(routeBean.highlightedPosition());
        circle.setLayoutX(mapViewParameters.get().viewX(PointWebMercator.ofPointCh(pointCh)));
        circle.setLayoutY(mapViewParameters.get().viewY(PointWebMercator.ofPointCh(pointCh)));
    }

    private void setEvents(){
        circle.setOnMouseClicked(event-> {
            //TODO demander si y a mieux a faire q uiterer comme ca
            Point2D point2D = circle.localToParent(event.getX(), event.getY());
            int nodeId = routeBean.route().get().nodeClosestTo(routeBean.highlightedPosition());
            boolean alreadyAWayPoint = false;
            for(WayPoint wayPoint : routeBean.waypoints) {
                if (wayPoint.closestNodeId() == nodeId) {
                    errorConsumer.accept("Un point de passage est déjà présent à cet endroit !");
                    alreadyAWayPoint=true;
                    break;
                }
            }
            if(!alreadyAWayPoint) {
                PointWebMercator pointWebMercator = mapViewParameters.get().pointAt(point2D.getX(), point2D.getY());
                routeBean.waypoints.add(new WayPoint(pointWebMercator.toPointCh(), nodeId));
            }
        });

        mapViewParameters.addListener((property, previousV, newV) -> {
            if(!previousV.topLeft().equals(newV.topLeft())) {
                double deltaX = newV.topLeft().getX() - previousV.topLeft().getX();
                double deltaY = newV.topLeft().getY() - previousV.topLeft().getY();
                polyline.setLayoutX(polyline.getLayoutX()+deltaX);
                polyline.setLayoutY(polyline.getLayoutY()+deltaY);
            }

            if(previousV.zoomLevel()!=newV.zoomLevel()) {
                createPointsCoordinates();
            }

            positionateCircle();
        });

        routeBean.highlightedPosition.addListener((property, previousV, newV) -> positionateCircle());

        routeBean.route().addListener((property, previousV, newV) -> {
            if(newV == null){
                polyline.setVisible(false);
                circle.setVisible(false);
            }
            else{
                polyline.setVisible(true);
                circle.setVisible(true);
                positionateCircle();
                createPointsCoordinates();
            }
        });
    }

}
