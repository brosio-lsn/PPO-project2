package ch.epfl.javelo.gui;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.routing.*;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.util.Pair;

import java.util.*;

public final class RouteBean {
    private static final int MAX_STEP_LENGTH = 5;
    /**
     * routeComputer to use to compute the itineraries between the multiple waypoints.
     */
    private final RouteComputer routeComputer;

    public ObservableList<WayPoint> getWaypoints() {
        return waypoints;
    }

    /**
     * list of waypoints to compute the itinerary between.
     */
    private final ObservableList<WayPoint> waypoints;
    /**
     * itinerary linking all the waypoints on the map.
     */
    private final ObjectProperty<Route> route;
    /**
     * highlighted position draw by a circle on the itinerary
     */
    public DoubleProperty highlightedPosition;
    /**
     * elevationProfile of the route.
     */
    private final ObjectProperty<ElevationProfile> elevationProfile;
    /**
     * Cache used to avoid repetitive computations of the best itineraries between two points.
     */
    private final LinkedHashMap<Pair<Integer, Integer>, Route> bestRouteCache;
    /**
     * initial size of the bestRouteCache
     */
    private final static int INITIAL_CAPACITY = 30;
    /**
     * default load factor of any LinkedHashMap -> to be used in the constructor of bestRouteCache.
     */
    private final static float LOAD_FACTOR = 0.75f;
    /**
     * determines whether any iterator accesses the elements in a LinkedHashMap by their reversed order of access (true here)
     */
    private final static boolean ELDEST_ACCES = true;
    /**
     * List representing the multiple itineraries linking the multiple waypoints on the map.
     */
    List<Route> theRoutes;

    public RouteBean(RouteComputer routeComputer) {
        this.routeComputer = routeComputer;
        bestRouteCache = new LinkedHashMap<>(INITIAL_CAPACITY, LOAD_FACTOR, ELDEST_ACCES);
        route = new SimpleObjectProperty<>();
        waypoints = FXCollections.observableArrayList();
        elevationProfile = new SimpleObjectProperty<>();
        highlightedPosition = new SimpleDoubleProperty();
        theRoutes = new ArrayList<>();
        installListeners();
    }

    /**
     * installs the listener on the list of waypoints, and makes it so the program reacts
     * to any change it might have.
     */
    private void installListeners() {
        waypoints.addListener((ListChangeListener<WayPoint>) c -> updateWaypointsList());
    }

    private void updateWaypointsList() {
        theRoutes.clear();
        if (waypoints.size() >= 2) {
            for (int i = 0; i < waypoints.size() - 1; i++) {
                int nodeIdOfFirstWaypoint = waypoints.get(i).closestNodeId();
                int nodeIdOfSecondWaypoint = waypoints.get(i + 1).closestNodeId();
                if (!(nodeIdOfFirstWaypoint == nodeIdOfSecondWaypoint)) {
                    if (!(bestRouteCache.containsKey(new Pair<>(nodeIdOfFirstWaypoint, nodeIdOfSecondWaypoint)))) {
                        Route bestRouteBetween = routeComputer.bestRouteBetween(nodeIdOfFirstWaypoint, nodeIdOfSecondWaypoint);
                        if (bestRouteBetween == null) {
                            nullifyProperties();
                            return;
                        }
                        bestRouteCache.put(new Pair<>(nodeIdOfFirstWaypoint, nodeIdOfSecondWaypoint), bestRouteBetween);
                        if (bestRouteCache.size() == INITIAL_CAPACITY) bestRouteCache.remove(bestRouteCache.keySet().iterator().next());

                    }
                    theRoutes.add(bestRouteCache.get(new Pair<>(nodeIdOfFirstWaypoint, nodeIdOfSecondWaypoint)));
                }

            }
            route.set(new MultiRoute(theRoutes));
            computeElevationProfile();
        } else {
            nullifyProperties();
        }
    }

    /**
     * Nullifies the properties of this bean in case the list of waypoints cannot compute a route.
     */
    private void nullifyProperties() {
        theRoutes.clear();
        route.setValue(null);
        elevationProfile.set(null);
    }

    /**
     * Computes the elevation profile of the updated route.
     */
    private void computeElevationProfile() {
        elevationProfile.set(ElevationProfileComputer.elevationProfile(route.get(), MAX_STEP_LENGTH));
    }

    /**
     * returns the highlightedPositionProperty of this routeBean.
     *
     * @return the highlightedPositionProperty of this routeBean.
     */
    public DoubleProperty highlightedPositionProperty() {
        return highlightedPosition;
    }

    /**
     * Returns the position on the route of the highlighted point.
     *
     * @return the position on the route of the highlighted point.
     */
    public double highlightedPosition() {
        return highlightedPosition.get();
    }

    /**
     * Sets the highlighted position on the route to the given position.
     *
     * @param position position on the route to set the highlighted position at.
     */
    public void setHighlightedPosition(double position) {
        if (route.get() != null) highlightedPosition.set(Math2.clamp(0, position, route.get().length()));
    }

    /**
     * Returns the itinerary between all the waypoints in the list of waypoints. It is on read only so that it cannot
     * be accessed from the exterior, and no mischievous computations can be made.
     *
     * @return the itinerary between all the waypoints
     */
    public ReadOnlyObjectProperty<Route> route() {
        return route;
    }

    /**
     * Returns the elevationProfile of the route linking all the waypoints in the list of waypoints. It
     * is on read only so that it cannot be accessed from the exterior, and no mischievous computations can be made.
     *
     * @return the elevationProfile of the route linking all the waypoints in the list of waypoints.
     */
    public ReadOnlyObjectProperty<ElevationProfile> elevationProfile() {
        return elevationProfile;
    }

    public int indexOfNonEmptySegmentAt(double position) {
        int index = route().get().indexOfSegmentAt(position);
        for (int i = 0; i <= index; i += 1) {
            int n1 = waypoints.get(i).closestNodeId();
            int n2 = waypoints.get(i + 1).closestNodeId();
            if (n1 == n2) index += 1;
        }
        return index;
    }
}
