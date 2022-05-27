package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
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
//todo commenter les attributs
    /**
     * svg for the group exterior
     */
    private static final String SVG_EXTERIOR_STRING = "M-8-20C-5-14-2-7 0 0 2-7 5-14 8-20 20-40-20-40-8-20";

    /**
     * svg for the group interior
     */
    private static final String SVG_INTERIOR_STRING = "M0-23A1 1 0 000-29 1 1 0 000-23";

    /**
     * error message for invalid waypoint placement
     */
    private static final String ERROR_MESSAGE = "Aucune route à proximité !";
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
    private final ObservableList<WayPoint> wayPointsList;

    /**
     * an object for reporting errors
     */
    private final Consumer<String> errorConsumer;

    /**
     * the pane containing all waypoints
     */
    private final Pane pane;

    /**
     * search distance for node closest to
     */
    private final static int SEARCH_DISTANCE_NODE_CLOSEST_TO = 1000;

    /**
     * property recording the coordinates of the mouse on the last event it indulged in
     */
    private ObjectProperty<Point2D> mouseOnLastEvent;

    /**
     * constructor of WaypointsManager
     *
     * @param graph             the graph of the route
     * @param mapViewParameters a JavaFX property containing the parameters of the displayed map
     * @param wayPointsList     the (observable) list of all waypoints
     * @param errorConsumer     an object for reporting errors
     */
    public WaypointsManager(Graph graph, ObjectProperty<MapViewParameters> mapViewParameters, ObservableList<WayPoint> wayPointsList, Consumer<String> errorConsumer) {
        this.graph = graph;
        this.mapViewParameters = mapViewParameters;
        this.wayPointsList = wayPointsList;
        this.errorConsumer = errorConsumer;
        this.mouseOnLastEvent = new SimpleObjectProperty<>();
        pane = createPane();
        createMarkers();
        this.mouseOnLastEvent=new SimpleObjectProperty<>();
        initiateListeners();
    }

    /**
     * creates the markers corresponding to the waypoints contained in observableList
     * and adds them as children to the pane
     */
    private void createMarkers() {
        List<Node> markers = new ArrayList<>();
        int size = wayPointsList.size();
        //todo mieux de faire un forEach avec un i que j incremente tt seul?(nnon)
        //todo nommage de constantes ?!?!?!?!?
        for (int i = 0; i < size; ++i) {
            WayPoint wayPoint = wayPointsList.get(i);
            Group group = createMarkerGroup(wayPoint);
            if (i == 0) group.getStyleClass().add("first");
            else if (i == size - 1) group.getStyleClass().add("last");
            else group.getStyleClass().add("middle");
            markers.add(group);
        }
        pane.getChildren().setAll(markers);
    }

    /**
     * creates the initial pane (used in constructor)
     *
     * @return the initial pane
     */
    private Pane createPane() {
        Pane p = new Pane();
        p.setPickOnBounds(false);
        return p;
    }

    /**
     * returns the pane containing all the wayPoints
     *
     * @return the pane containing all the wayPoints
     */
    public Pane pane() {
        return pane;
    }

    /**
     * initiates the listeners (used in constructor)
     */
    private void initiateListeners() {
        mapViewParameters.addListener((property, previousV, newV) -> relocateMarkers(newV));
        wayPointsList.addListener((ListChangeListener<WayPoint>) c -> createMarkers());
    }

    /**
     * takes as arguments the x and y coordinates of an initial point and adds a new wayPoint at the closest node in the graph
     *
     * @param x x coordinate of the initial point
     * @param y y coordinate of the initial point
     */
    public void addWayPoint(double x, double y) {
        PointWebMercator pointWebMercator = mapViewParameters.get().pointAt(x, y);
        PointCh pointCh = pointWebMercator.toPointCh();
        if (pointCh != null) {
            int nodeId = graph.nodeClosestTo(pointCh, SEARCH_DISTANCE_NODE_CLOSEST_TO);
            if (nodeId == -1) {
                errorConsumer.accept(ERROR_MESSAGE);
                return;
            }
            WayPoint wayPoint = new WayPoint(pointCh, nodeId);
            wayPointsList.add(wayPoint);
        } else errorConsumer.accept(ERROR_MESSAGE);
    }


    /**
     * relocates the markers according to the new mapviewParameters given in argument
     *
     * @param mapViewParameters the new mapviewParameters
     */
    private void relocateMarkers(MapViewParameters mapViewParameters) {
        Iterator<WayPoint> itWaypoints = wayPointsList.iterator();
        for (Node node : pane.getChildren()) {
            WayPoint wayPoint = itWaypoints.next();
            PointWebMercator nodePointWebMercator = PointWebMercator.ofPointCh(wayPoint.point());
            node.setLayoutX(mapViewParameters.viewX(nodePointWebMercator));
            node.setLayoutY(mapViewParameters.viewY(nodePointWebMercator));
        }
    }

    /**
     * creates a regular marker group
     *
     * @return a regular marker group
     */
    private Group createMarkerGroup(WayPoint wayPoint) {
        SVGPath exterior = new SVGPath();
        exterior.setContent(SVG_EXTERIOR_STRING);
        exterior.getStyleClass().add("pin_outside");

        SVGPath interior=new SVGPath();
        interior.setContent(SVG_INTERIOR_STRING);
        interior.getStyleClass().add("pin_inside");

        Group group=new Group(exterior, interior);
        group.getStyleClass().add("pin");

        PointWebMercator nodePointWebMercator = PointWebMercator.ofPointCh(wayPoint.point());
        group.setLayoutX(mapViewParameters.get().viewX(nodePointWebMercator));
        group.setLayoutY(mapViewParameters.get().viewY(nodePointWebMercator));

        ObjectProperty<WayPoint> draggedWayPoint = new SimpleObjectProperty<>();
        DoubleProperty yBeforeDrag= new SimpleDoubleProperty();
        DoubleProperty xBeforeDrag = new SimpleDoubleProperty();

        group.setOnMouseClicked(event->{
            if (event.isStillSincePress())wayPointsList.remove(wayPoint);
        });
        ObjectProperty<WayPoint> draggedWayPoint = new SimpleObjectProperty<>();
        group.setOnMousePressed(event -> {
            xBeforeDrag = group.getLayoutX();
            yBeforeDrag = group.getLayoutY();

        group.setOnMousePressed(event-> {
            xBeforeDrag.set(group.getLayoutX());
            yBeforeDrag.set(group.getLayoutY());
            draggedWayPoint.set(wayPoint);
            mouseOnLastEvent.set(new Point2D(event.getX(), event.getY()));
        });
        group.setOnMouseDragged(event-> {
            double deltaX = event.getX() - mouseOnLastEvent.get().getX();
            double deltaY = event.getY() - mouseOnLastEvent.get().getY();
            group.setLayoutX(group.getLayoutX()+deltaX);
            group.setLayoutY(group.getLayoutY()+deltaY);
            mouseOnLastEvent.get().add(deltaX, deltaY);
        });

        group.setOnMouseReleased(event-> {
            if (!event.isStillSincePress()) {
                PointCh pointCh = mapViewParameters.get().pointAt(group.getLayoutX(), group.getLayoutY()).toPointCh();
                int nodeId=-1;
                if (pointCh!=null)nodeId= graph.nodeClosestTo(pointCh, SEARCH_DISTANCE_NODE_CLOSEST_TO);
                if (nodeId == -1) {
                    group.setLayoutX(xBeforeDrag.doubleValue());
                    group.setLayoutY(yBeforeDrag.doubleValue());
                    errorConsumer.accept(ERROR_MESSAGE);
                }
                else wayPointsList.set(wayPointsList.indexOf(draggedWayPoint.get()), new WayPoint(pointCh, nodeId));
            }
        });
        return group;
    }