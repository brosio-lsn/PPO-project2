package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.shape.SVGPath;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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
    private final ObservableList<WayPoint> observableList;

    /**
     * an object for reporting errors
     */
    private final Consumer<String> errorConsumer;

    /**
     * the pane containing all waypoints
     */
    private final Pane pane;

    /**
     * search distance for node closest to, used in the method addWayPoint
     */
    private final static int SEARCH_DISTANCE_NODE_CLOSEST_TO_1 = 500;

    /**
     * search distance for node closest to, used when a marker is repositioned after a drag
     */
    private final static int SEARCH_DISTANCE_NODE_CLOSEST_TO_2 = 1000;

    /**
     * the current wayPoint that is being dragged
     */
    private WayPoint draggedWayPoint;

    /**
     * pre-drag x coordinate of the marker that is being dragged
     */
    private double xBeforeDrag;

    /**
     * pre-drag y coordinate of the marker that is being dragged
     */
    private double yBeforeDrag;

    private ObjectProperty<Point2D> mouseOnLastEvent;

    //todo immuabilité (je peux faire un copyOf dde la liste?
    //todo voir si j ajoute les node bien (genre sur les graph node ou pas)
    //todo demander que faire si on ajoute un pt dans le lac (j ai l error lancée mais j ajoute qd mm?)
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
        mapViewParameters.addListener((property, previousV, newV) -> relocateMarkers(newV));

        this.observableList=observableList;
        //TODO demander si comme ca ca passe tu connnais
        observableList.addListener((ListChangeListener) change -> createMarkers());

        this.errorConsumer=errorConsumer;
        pane=new Pane();
        pane.setPickOnBounds(false);
        createMarkers();

        this.mouseOnLastEvent=new SimpleObjectProperty<>();

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
        int nodeId=graph.nodeClosestTo(pointWebMercator.toPointCh(), SEARCH_DISTANCE_NODE_CLOSEST_TO_1);
        if(nodeId==-1) {
            errorConsumer.accept("Aucune route à proximité !");
            return;
        }
        WayPoint wayPoint = new WayPoint(pointWebMercator.toPointCh(), nodeId);

        Group group = createMarkerGroup(wayPoint);
        if(observableList.size()>0) {
            group.getStyleClass().add("last");
            Node previousGroup = pane.getChildren().get(pane.getChildren().size()-1);
            previousGroup.getStyleClass().remove("last");
            previousGroup.getStyleClass().add("middle");
        }
        else group.getStyleClass().add("first");
        //todo demander si je dois bien setLayout en fonction des corrdonées du noeau javelo, pas du point initial
        pane.getChildren().add(group);
        observableList.add(wayPoint);
    }

/*    *//**
     * creates the pane containing all the waypoints in observableList
     * @return the pane containing all the waypoints in observableList
     *//*
    private Pane createPane(){
        this.pane=new Pane();
        pane.setPickOnBounds(false);
        createMarkers();
        return pane;
    }*/

    /**
     * relocates the markers according to the new mapviewParameters given in argument
     * @param mapViewParameters the new mapviewParameters
     */
    private void relocateMarkers(MapViewParameters mapViewParameters){
        List<Node> markers = new ArrayList<>();
        Iterator<WayPoint> itWaypoints = observableList.iterator();
        Iterator<Node> itNode = pane.getChildren().iterator();
        while(itNode.hasNext()){
            Node node = itNode.next();
            WayPoint wayPoint = itWaypoints.next();
            PointWebMercator nodePointWebMercator = PointWebMercator.ofPointCh(wayPoint.point());
            node.setLayoutX(mapViewParameters.viewX(nodePointWebMercator));
            node.setLayoutY(mapViewParameters.viewY(nodePointWebMercator));
            markers.add(node);
        }
        pane.getChildren().setAll(markers);
    }

    /**
     * creates the markers corresponding to the waypoints contained in observableList
     * and adds them as children to the pane
     */
    private void createMarkers (){
        List<Node> markers = new ArrayList<>();
        int size = observableList.size();
        for(int i =0; i<observableList.size();++i){
            WayPoint wayPoint=observableList.get(i);

            Group group = createMarkerGroup(wayPoint);
            if(i==0) group.getStyleClass().add("first");
                //todo je check si i est superor a 0 aussi car sinon tu peux etre first et last
            else if(i==size-1 && i!=0) group.getStyleClass().add("last");
            else group.getStyleClass().add("middle");
            markers.add(group);
        }
        pane.getChildren().setAll(markers);
    }

    /**
     * creates a regular marker group
     * @return a regular marker group
     */
    private Group createMarkerGroup (WayPoint wayPoint){
        SVGPath exterior=new SVGPath();
        exterior.setContent("M-8-20C-5-14-2-7 0 0 2-7 5-14 8-20 20-40-20-40-8-20");
        exterior.getStyleClass().add("pin_outside");

        SVGPath interior=new SVGPath();
        interior.setContent("M0-23A1 1 0 000-29 1 1 0 000-23");
        interior.getStyleClass().add("pin_inside");

        Group group=new Group(exterior, interior);
        group.getStyleClass().add("pin");

        PointWebMercator nodePointWebMercator = PointWebMercator.ofPointCh(wayPoint.point());
        group.setLayoutX(mapViewParameters.get().viewX(nodePointWebMercator));
        group.setLayoutY(mapViewParameters.get().viewY(nodePointWebMercator));

        group.setOnMouseClicked(event->{
            if (event.isStillSincePress())observableList.remove(wayPoint);
            if (event.isStillSincePress())System.out.println("deleted");
        });

        group.setOnMousePressed(event-> {
            xBeforeDrag=group.getLayoutX();
            yBeforeDrag=group.getLayoutY();
            draggedWayPoint=wayPoint;
            System.out.println("pressed");
            mouseOnLastEvent.set(new Point2D(event.getX(), event.getY()));
        });

        group.setOnMouseDragged(event-> {
            double deltaX = event.getX() - mouseOnLastEvent.get().getX();
            double deltaY = event.getY() - mouseOnLastEvent.get().getY();
            group.setLayoutX(group.getLayoutX()+deltaX);
            group.setLayoutY(group.getLayoutY()+deltaY);
            mouseOnLastEvent.get().add(deltaX, deltaY);
            System.out.println("dragg");
        });

        group.setOnMouseReleased(event-> {
            if (!event.isStillSincePress()) {
                PointCh pointCh = mapViewParameters.get().pointAt(group.getLayoutX(), group.getLayoutY()).toPointCh();
                int nodeId = graph.nodeClosestTo(pointCh, SEARCH_DISTANCE_NODE_CLOSEST_TO_2);
                if (nodeId == -1) {
                    System.out.println("error");
                    group.setLayoutX(xBeforeDrag);
                    group.setLayoutY(yBeforeDrag);
                    errorConsumer.accept("Aucune route à proximité !");
                }
                else {
                    observableList.set(observableList.indexOf(draggedWayPoint), new WayPoint(pointCh, nodeId));
                }
            }
        });
        return group;
    }


}
