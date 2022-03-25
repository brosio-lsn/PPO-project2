package ch.epfl.javelo.routing;

import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.projection.PointCh;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

public final class MultiRoute {
    private final List<Route> segments;

    public MultiRoute(List<Route> segments) {
        Preconditions.checkArgument(!segments.isEmpty());
        this.segments = List.copyOf(segments);
    }

    /**
     * Returns the index of the segment at a given position.
     *
     * @param position position in the route, which belongs to the segment returned.
     * @return the index of the segment at a given position.
     */

    public int indexOfSegmentAt(double position) {
        double distance = 0;
        int index = 0;
        while (distance <= position) {
            distance += segments.get(index).length();
            index++;
        }
        return segments.get(index - 1).indexOfSegmentAt(position) + (index - 1);
    }

    /**
     * Returns the total length of the itinerary
     *
     * @return the total length of the itinerary, by summing up the length of all its edges.
     */
    public double length() {
        double longueur = 0;
        for (Route route : segments) {
            longueur += route.length();
        }
        return longueur;
    }

    /**
     * Returns the totality of the itinerary's edges.
     *
     * @return the totality of the itinerary's edges.
     */

    public List<Edge> edges() {
        List<Edge> edges = new ArrayList<>();
        for (Route route : segments) {
            edges.addAll(route.edges());
        }
        return List.copyOf(edges);
    }

    /**
     * Returns the totality of the itinerary's points, which are located on the beginning and on the end of each edge.
     *
     * @return the totality of the itinerary's points.
     */

    public List<PointCh> points() {
        List<PointCh> pointRoute = new ArrayList<>();
        for (Edge edge : edges()) {
            if (!pointRoute.contains(edge.fromPoint())) pointRoute.add(edge.fromPoint());
            if (!pointRoute.contains(edge.toPoint())) pointRoute.add(edge.toPoint());
        }
        return pointRoute;
    }

    /**
     * Returns the PointCh at the given position along the itinerary.
     *
     * @param position position to search the PointCh at.
     * @return the PointCh at the given position along the itinerary.
     */
    public PointCh pointAt(double position) {
        return segments.get(indexOfSegmentAt(position)).pointAt(position);
    }

    /**
     * Returns the elevation of the itinerary at the given position.
     *
     * @param position position along the itinerary to get the elevation from.
     * @return the elevation of the itinerary at the given position.
     */
    public double elevationAt(double position) {
        return segments.get(indexOfSegmentAt(position)).elevationAt(position);
    }

    /**
     * Returns the identity of the node closest to a given position along the itinerary.
     *
     * @param position position to search the closest node to.
     * @return the identity of the node closest to a given position along the itinerary.
     */
    public int nodeClosestTo(double position) {
        return segments.get(indexOfSegmentAt(position)).nodeClosestTo(position);
    }

    /**
     * Returns the RoutePoint closest to a given point on the itinerary.
     *
     * @param point given point to find its closest RoutePoint on the itinerary.
     * @return the closest RoutePoint to a given point on the itinerary.
     */
    public RoutePoint pointClosestTo(PointCh point) {
        double distance = Double.POSITIVE_INFINITY;
        RoutePoint min = RoutePoint.NONE;
        for (Route segment : segments) {
            if (segment.pointAt(segment.pointClosestTo(point).position()).distanceTo(point) < distance) {
                min = segment.pointClosestTo(point);
                distance = segment.pointAt(segment.pointClosestTo(point).position()).distanceTo(point);
            }
        }
        return min;
    }
}
