package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import javafx.beans.property.ObjectProperty;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polyline;

import java.util.function.Consumer;

public final class RouteManager {

    private final RouteBean routeBean;

    private final ObjectProperty<MapViewParameters> mapViewParameters;

    private final Consumer<String> errorConsumer;

    public RouteManager(RouteBean routeBean, ObjectProperty<MapViewParameters> mapViewParameters, Consumer<String> errorConsumer){
        this.routeBean=routeBean;
        this.mapViewParameters=mapViewParameters;
        this.errorConsumer=errorConsumer;
    }

    private void createPane(){
        Pane pane = new Pane();
        pane.setPickOnBounds(false);
        Polyline polyline = new Polyline();
        polyline.setId("route");
        polyline.getPoints().addAll(createPointsCoordinates());
        pane.getChildren().add(polyline);
        Circle circle

        // recontruite full la polyline si on chnage itineraire/zoom et sinon juste la déclaer
        //comment centrer mon cercle
        //comment bouger la polyline (il faut setLayout le Pane et recgarder les coordonnées top left de mapviewParameters)
    }

    private Double[] createPointsCoordinates(){
        Double [] arrayWithCoordinates = new Double[routeBean.route().get().points().size()*2];
        int i=0;
        for(PointCh pointCh : routeBean.route().get().points()){
            arrayWithCoordinates[i]= mapViewParameters.get().viewX(PointWebMercator.ofPointCh(pointCh));
            ++i;
            arrayWithCoordinates[i]= mapViewParameters.get().viewY(PointWebMercator.ofPointCh(pointCh));
            ++i;
        }
        return arrayWithCoordinates;
    }

    private Circle createCircle(){
        Circle circle = new Circle();
        circle.setId("highlight");
        circle.setRadius(5);
        PointCh pointCh= routeBean.route().get().pointAt(routeBean.highlightedPosition());
        circle.setLayoutX(mapViewParameters.get().viewX(PointWebMercator.ofPointCh(pointCh)));
        circle.setLayoutY(mapViewParameters.get().viewY(PointWebMercator.ofPointCh(pointCh)));
        return circle;
    }

}
