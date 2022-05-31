package ch.epfl.javelo.gui;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import ch.epfl.javelo.projection.SwissBounds;
import ch.epfl.javelo.projection.WebMercator;
import ch.epfl.javelo.routing.RoutePoint;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.function.Consumer;
/**
 * @author Louis ROCHE (345620)
 * @author Ambroise AIGUEPERSE (341890)
 *
 * handels the display of the annotated map
 */
public final class AnnotatedMapManager {
    //private static final double[] DISTANCES_FOR_SCALE = {10000, 5000, 3600, 1600, 800, 400, 200, 100, 50, 20, 10, 5, 2, 1, 0.5, 0.2, 0.1, 0.05, 0.02, 0.01};
    private static final double[] DISTANCES_FOR_SCALE = {10, 20, 50, 100, 200, 500, 1000, 2000, 3000, 5000, 7000, 10000, 15000, 20000, 35000, 50000, 100000, 200000};
    private static final double MouseNotCloseToRoute = Double.NaN;
    private static final int MAXIMAL_PIXEL_DISTANCE_FOR_MOUSE = 15;
    private static final double EARTH_RADIUS_IN_KM = 6372.795477598;
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
    private final DoubleProperty positionAlongRoute;
    private final GridPane statsPane;
    private IntegerProperty distanceScaleIndex;

    /**
     * constructor of the class
     * @param graph the with all the edges, node...
     * @param tileManager the tile manager
     * @param routeBean the bean of the displayed route
     * @param consumer a consumer to display errors
     */
    public AnnotatedMapManager(Graph graph, TileManager tileManager, RouteBean routeBean, Consumer<String> consumer) {
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
        this.statsPane = new StatsPane(routeBean, consumer).pane();
        distanceScaleIndex= new SimpleIntegerProperty(9);
        createPane();
        setEvents();
        createButtons();
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
     * creates the pane of the annotated map
     */
    private void createPane(){
        pane.getStylesheets().add("map.css");
        pane.getChildren().addAll(baseMapManager.pane(), routeManager.pane(), waypointsManager.pane());
    }

    /**
     * creation of the buttons for the bonus
     */
    private void createButtons(){
        Button removeAllBtn = new Button("remove all waypoints");
        Button reverseRoute = new Button("reverse route");
        Button ecoStat = new Button("display eco-stats");
        Line line = new Line(pane.getWidth()-100, 35, pane.getWidth(), 35);
        Text scale = new Text();
        Pane buttonPane = new Pane(removeAllBtn,reverseRoute, ecoStat, line, scale);
        buttonPane.setPickOnBounds(false);
        pane.getChildren().add(buttonPane);

        scale.textProperty().bind(Bindings.createStringBinding(()->{

            //double distance = DISTANCES_FOR_SCALE[distanceScaleIndex.get()];
            double distance = DISTANCES_FOR_SCALE[findIndex()];
            return distance<1000 ? String.valueOf(distance) +" m": String.valueOf(distance/1000) +" km";
        }, distanceScaleIndex));

        line.startXProperty().bind(Bindings.createDoubleBinding(()->pane.getWidth()-findLength(), mapViewParametersP, pane.widthProperty()));
        line.endXProperty().bind(Bindings.createDoubleBinding(()->pane.getWidth(),mapViewParametersP, pane.widthProperty()));
        scale.layoutXProperty().bind(Bindings.createDoubleBinding(()->line.startXProperty().get(), line.startXProperty()));
        scale.setLayoutY(line.startYProperty().get()+15);
        //System.out.println(line.getLayoutY());
        //scale.setLayoutY(300);
        //scale.setLayoutX(400);


        removeAllBtn.setOnAction(event -> routeBean.getWaypoints().clear());
        removeAllBtn.visibleProperty().bind(Bindings.createBooleanBinding(() ->routeBean.getWaypoints().size()>0, routeBean.getWaypoints()));

        reverseRoute.setOnAction(event -> {
            ArrayList<WayPoint> reverseW = new ArrayList<>(routeBean.getWaypoints());
            Collections.reverse(reverseW);
            routeBean.getWaypoints().setAll(reverseW);
        });

        reverseRoute.visibleProperty().bind(Bindings.createBooleanBinding(() ->routeBean.route().get()!=null, routeBean.route()));

        ecoStat.visibleProperty().bind(Bindings.createBooleanBinding(() ->routeBean.route().get()!=null, routeBean.route()));
        ecoStat.setOnAction(event -> {
            if(pane.getChildren().contains(statsPane)){
                pane.getChildren().remove(statsPane);
                ecoStat.textProperty().set("display eco-stats");
            }
            else {
                pane.getChildren().add(pane.getChildren().size()-1, statsPane);
                ecoStat.textProperty().set("hide eco-stats");
            }
        });
        routeBean.route().addListener((property, oldV, newV)->{
            if(newV==null) {
                pane.getChildren().remove(statsPane);
                ecoStat.textProperty().set("display eco-stats");
            }
        });

        removeAllBtn.setLayoutX(0);
        removeAllBtn.setLayoutY(25);
        ecoStat.setLayoutX(0);
        ecoStat.setLayoutY(50);
        reverseRoute.setLayoutX(0);
        reverseRoute.setLayoutY(75);
    }

    private int findLength(){
        //System.out.println(mapViewParametersP.get().zoomLevel());
        //System.out.println("called");
        distanceScaleIndex.set(-1);
        double distanceInPixels;
        do{
            //System.out.println(DISTANCES_FOR_SCALE[i]);
            distanceScaleIndex.set(distanceScaleIndex.get()+1);
            PointWebMercator pointWebMercatorExtrem = PointWebMercator.ofPointCh(new PointCh(SwissBounds.MIN_E, 1150000));
            PointWebMercator pointWebMercatorExtend = PointWebMercator.ofPointCh(new PointCh(SwissBounds.MIN_E+DISTANCES_FOR_SCALE[distanceScaleIndex.get()], 1150000));
            distanceInPixels =mapViewParametersP.get().viewX(pointWebMercatorExtend)-mapViewParametersP.get().viewX(pointWebMercatorExtrem);
            //distanceInPixels = Math2.norm(uX, uY)/Math.pow(2, mapViewParametersP.get().zoomLevel()+8)*pane.getWidth();
            //System.out.println(distanceInPixels);
        }while(distanceInPixels<60 && distanceScaleIndex.get()<DISTANCES_FOR_SCALE.length);
        return (int) distanceInPixels;
    }

    private int findIndex(){
        //System.out.println(mapViewParametersP.get().zoomLevel());
        //System.out.println("called");
        int i=-1;
        double distanceInPixels;
        do{
            //System.out.println(DISTANCES_FOR_SCALE[i]);
            i++;
            PointWebMercator pointWebMercatorExtrem = PointWebMercator.ofPointCh(new PointCh(SwissBounds.MIN_E, 1150000));
            PointWebMercator pointWebMercatorExtend = PointWebMercator.ofPointCh(new PointCh(SwissBounds.MIN_E+DISTANCES_FOR_SCALE[i], 1150000));
            distanceInPixels =mapViewParametersP.get().viewX(pointWebMercatorExtend)-mapViewParametersP.get().viewX(pointWebMercatorExtrem);
            //distanceInPixels = Math2.norm(uX, uY)/Math.pow(2, mapViewParametersP.get().zoomLevel()+8)*pane.getWidth();
            //System.out.println(distanceInPixels);
        }while(distanceInPixels<60 && i<DISTANCES_FOR_SCALE.length);
        return i;
    }

    /**
     * sets all the events for the class
     */
    private void setEvents(){
        pane.setOnMouseMoved(event-> {
            mouseOnLastEvent.set(new Point2D(event.getX() , event.getY()));
        });

        pane.setOnMouseExited(event-> {
            mouseOnLastEvent.set(null);
        });

        positionAlongRoute.bind(Bindings.createDoubleBinding(this::updatePositionAlongRoute, mouseOnLastEvent, routeBean.route(), mapViewParametersP));
    }

    private double updatePositionAlongRoute(){
        if(mapViewParametersP.get()==null || routeBean.route().get()==null || mouseOnLastEvent.get()==null || pane.getChildren().contains(statsPane))return MouseNotCloseToRoute;
        PointWebMercator pointWebMercator = mapViewParametersP.get().pointAt(mouseOnLastEvent.get().getX(), mouseOnLastEvent.get().getY());
        PointCh pointCh = pointWebMercator.toPointCh();
        if(pointCh==null) return MouseNotCloseToRoute;
        RoutePoint pointOnRoute = routeBean.route().get().pointClosestTo(pointCh);
        PointWebMercator pointWebMercatorOfPointCh = PointWebMercator.ofPointCh(pointOnRoute.point());
        double uX=mouseOnLastEvent.get().getX()-mapViewParametersP.get().viewX(pointWebMercatorOfPointCh);
        double uY=mouseOnLastEvent.get().getY()-mapViewParametersP.get().viewY(pointWebMercatorOfPointCh);
        double distanceInPixels = Math2.norm(uX, uY);
        if(distanceInPixels< MAXIMAL_PIXEL_DISTANCE_FOR_MOUSE){
            return pointOnRoute.position();}
        else return MouseNotCloseToRoute;
    }

}
