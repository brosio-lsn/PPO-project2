package ch.epfl.javelo.gui;

import ch.epfl.javelo.routing.*;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

public final class RouteBean {
    private final RouteComputer routeComputer;
    public ObservableList<WayPoint> waypoints;
    private ObjectProperty<Route> route;
    public DoubleProperty highlightedPosition;
    private ObjectProperty<ElevationProfile> elevationProfile;
    private final LinkedHashMap<Pair<Integer, Integer>, Route> bestRouteCache;
    private int oldSize;

    private final static int CAPACITY_OF_CACHE = 10;
    private final static float LOAD_FACTOR = 0.75f;
    private final static boolean ELDEST_ACCES = true;

    public RouteBean(RouteComputer routeComputer) {
        this.routeComputer = routeComputer;
        bestRouteCache = new LinkedHashMap<>(CAPACITY_OF_CACHE, LOAD_FACTOR, ELDEST_ACCES);
        installListeners();
    }

    public DoubleProperty highlightedPositionProperty() {
        return highlightedPosition;
    }

    public double highlightedPosition() {
        return highlightedPosition.get();
    }

    public void setHighlightedPosition(double s) {
        highlightedPosition.set(s);
    }

    //TODO est-ce vraiment une ReadOnlyObjectProperty ?
    public ReadOnlyObjectProperty<Route> route() {
        return route;
    }

    public ReadOnlyObjectProperty<ElevationProfile> elevationProfile() {
        return elevationProfile;
    }

    private void installListeners() {
        waypoints.addListener((ListChangeListener<WayPoint>) c -> {
            if (oldSize > (waypoints).size()) cleanCache();
            if (waypoints.size() >= 2) {
                for (int i = 0; i < waypoints.size() - 1; i++) {
                    int nodeIdOfFirstWaypoint = waypoints.get(i).closestNodeId();
                    int nodeIdOfSecondWaypoint = waypoints.get(i + 1).closestNodeId();
                    if (!(bestRouteCache.containsKey(new Pair<>(nodeIdOfFirstWaypoint, nodeIdOfSecondWaypoint))
                            || bestRouteCache.containsKey(new Pair<>(nodeIdOfSecondWaypoint, nodeIdOfFirstWaypoint)))) {
                        Route bestRouteBetween = routeComputer.bestRouteBetween(nodeIdOfFirstWaypoint, nodeIdOfSecondWaypoint);
                        bestRouteCache.put(new Pair<>(nodeIdOfFirstWaypoint, nodeIdOfSecondWaypoint), bestRouteBetween);
                    }
                }
                //removeExtraPairsInCache();
                route.set(new MultiRoute(List.copyOf(bestRouteCache.values())));
                computeElevationProfile();
            } else {
                nullifyProperties();
            }
            oldSize = waypoints.size();
        });

    }
 /*   private void removeExtraPairsInCache() {
        Iterator<Pair<Integer, Integer>> iterator = bestRouteCache.keySet().iterator();
        while (bestRouteCache.keySet().size() >10) {
            bestRouteCache.remove(iterator.next());
        }
    }

  */
    private void cleanCache() {
        List<Integer> nodeIds = new ArrayList<>();
        for (WayPoint w : waypoints) {
            nodeIds.add(w.closestNodeId());
        }
        for (Pair<Integer, Integer> pair : bestRouteCache.keySet()) {
            if (!nodeIds.contains(pair.getKey()) || !nodeIds.contains(pair.getValue())) {
                bestRouteCache.remove(pair);
                break;
            }
        }
    }

    private void nullifyProperties() {
        route = null;
        highlightedPosition.set(Double.NaN);
        elevationProfile = null;
    }
    private void computeElevationProfile() {
        elevationProfile.set(ElevationProfileComputer.elevationProfile(route.get(), 5));
    }
}
