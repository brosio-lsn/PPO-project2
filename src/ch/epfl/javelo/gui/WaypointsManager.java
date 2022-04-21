package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointWebMercator;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.shape.SVGPath;
import java.util.function.Consumer;
/**
 * represents a point of passage
 *
 * @author Louis ROCHE (345620)
 * @author Ambroise AIGUEPERSE (341890)
 */

public final class WaypointsManager {

    /**
     * the graph of the route
     */
    private final Graph graph;

    /**
     * a JavaFX property containing the parameters of the displayed map
     */
    private final ObjectProperty<MapViewParameters> mapViewParameters;

    /**
     * the (observable) list of all waypoints
     */
    private final ObservableList observableList;

    /**
     * an object for reporting errors
     */
    private final Consumer<String> errorConsumer;

    /**
     * the pane containing all waypoints
     */
    private final Pane pane;

    //todo immuabilité (je peux faire un copyOf dde la liste?)

    /**
     * constructor of WaypointsManager
     * @param graph the graph of the route
     * @param mapViewParameters a JavaFX property containing the parameters of the displayed map
     * @param observableList the (observable) list of all waypoints
     * @param errorConsumer an object for reporting errors
     */
    public WaypointsManager(Graph graph, ObjectProperty<MapViewParameters> mapViewParameters, ObservableList<WayPoint> observableList, Consumer<String> errorConsumer){
        this.graph=graph;
        this.mapViewParameters=mapViewParameters;
        this.observableList=observableList;
        this.errorConsumer=errorConsumer;
        this.pane=createPane(observableList);
    }

    /**
     * returns the pane containing all the wayPoints
     * @return the pane containing all the wayPoints
     */
    public Pane pane(){
        return pane;
    }

    /**
     * takes as arguments the x and y coordinates of an initial point and adds a new wayPoint at the closest node in the graph
     * @param x x coordinate of the initial point
     * @param y y coordinate of the initial point
     */
    public void addWayPoint(double x, double y){
        PointWebMercator pointWebMercator = mapViewParameters.get().pointAt(x,y);
        //todo demander sur piazza pour la distance, le prof a donné 400
        int nodeId=graph.nodeClosestTo(pointWebMercator.toPointCh(), 400);
        WayPoint wayPoint = new WayPoint(pointWebMercator.toPointCh(), nodeId);

        Group group = createMarkerGroup();
        if(observableList.size()>0) {
            group.getStyleClass().add("last");
            Node previousGroup = pane.getChildren().get(pane.getChildren().size()-1);
            previousGroup.getStyleClass().remove("last");
            previousGroup.getStyleClass().add("middle");
        }
        else group.getStyleClass().add("first");
        //todo demander si je dois bien setLayout en fonction des corrdonées du noeau javelo, pas du point initial
        PointWebMercator nodePointWebMercator = PointWebMercator.ofPointCh(wayPoint.point());
        group.setLayoutX(mapViewParameters.get().viewX(nodePointWebMercator));
        group.setLayoutY(mapViewParameters.get().viewY(nodePointWebMercator));

        pane.getChildren().add(group);
        observableList.add(wayPoint);
    }

    /**
     * creates the pane containing all the waypoints in observableList
     * @param observableList the (observable) list of all waypoints
     * @return the pane containing all the waypoints in observableList
     */
    private Pane createPane(ObservableList<WayPoint> observableList){
        Pane pane=new Pane();
        int size = observableList.size();
        for(int i =0; i<observableList.size();++i){
            WayPoint wayPoint=observableList.get(i);

            Group group = createMarkerGroup();
            if(i==0) group.getStyleClass().add("first");
            //todo je check si i est superor a 0 aussi car sinon tu peux etre first et last
            else if(i==size-1 && i!=0) group.getStyleClass().add("last");
            else group.getStyleClass().add("middle");
            PointWebMercator pointWebMercator = PointWebMercator.ofPointCh(wayPoint.point());
            group.setLayoutX(mapViewParameters.get().viewX(pointWebMercator));
            group.setLayoutY(mapViewParameters.get().viewY(pointWebMercator));
            pane.setPickOnBounds(false);
            pane.getChildren().add(group);
        }
        return pane;
    }

    /**
     * creates a regular marker group
     * @return a regular marker group
     */
    private Group createMarkerGroup (){
        SVGPath exterior=new SVGPath();
        exterior.setContent("M-8-20C-5-14-2-7 0 0 2-7 5-14 8-20 20-40-20-40-8-20");
        exterior.getStyleClass().add("pin_outside");

        SVGPath interior=new SVGPath();
        interior.setContent("M0-23A1 1 0 000-29 1 1 0 000-23");
        interior.getStyleClass().add("pin_inside");

        Group group=new Group(exterior, interior);
        group.getStyleClass().add("pin");

        return group;
    }


}
