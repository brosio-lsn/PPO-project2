package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.projection.PointCh;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents multiple itineraries
 *
 * @author ROCHE Louis (345620)
 * @author AIGUEPERSE Ambroise (341890)
 */
public final class MultiRoute implements Route {
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
        double realPos = Math2.clamp(0, position, length());
        double sumOfDistances = 0;
        int index = 0;
        int sumOfPreviousSegments = 0;
        for (int i = 0; i < segments.size(); i++) {
            if (realPos >= sumOfDistances && realPos <= sumOfDistances + segments.get(i).length()) {
                index = i;
                break;
            } else sumOfDistances += segments.get(i).length();
        }
        for (int i = 0; i < index; i++) {
            Route route = segments.get(i);
            sumOfPreviousSegments += route.indexOfSegmentAt(route.length()) + 1;
        }
        double relativePosition = computeRelativePositionOnSegment(index, realPos);
        return segments.get(index).indexOfSegmentAt(relativePosition) + sumOfPreviousSegments;
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
        double realPos = Math2.clamp(0, position, length());
        int index = indexOfRoadAt(realPos);
        double relativePosition = computeRelativePositionOnSegment(index, realPos);
        return segments.get(index).pointAt(relativePosition);
    }

    /**
     * Returns the elevation of the itinerary at the given position.
     *
     * @param position position along the itinerary to get the elevation from.
     * @return the elevation of the itinerary at the given position.
     */
    public double elevationAt(double position) {
        double realPos = Math2.clamp(0, position, length());
        int index = indexOfRoadAt(realPos);
        double relativePosition = computeRelativePositionOnSegment(index, realPos);
        return segments.get(index).elevationAt(relativePosition);
    }


    /**
     * Returns the identity of the node closest to a given position along the itinerary.
     *
     * @param position position to search the closest node to.
     * @return the identity of the node closest to a given position along the itinerary.
     */
    public int nodeClosestTo(double position) {
        double realPos = Math2.clamp(0, position, length());
        int indexOfRoad = indexOfRoadAt(realPos);
        double relativePosition = computeRelativePositionOnSegment(indexOfRoad, realPos);
        return segments.get(indexOfRoad).nodeClosestTo(relativePosition);
    }

    /**
     * Returns the RoutePoint closest to a given point on the itinerary.
     *
     * @param point given point to find its closest RoutePoint on the itinerary.
     * @return the closest RoutePoint to a given point on the itinerary.
     */
    public RoutePoint pointClosestTo(PointCh point) {
        RoutePoint routePointClosestTo = RoutePoint.NONE;
        double shift = 0;
        for (Route segment : segments) {
            routePointClosestTo = routePointClosestTo.min(segment.pointClosestTo(point).withPositionShiftedBy(shift));
            shift+=segment.length();
        }
        return routePointClosestTo;
    }
    //PointCh pointClosestToOfSegment = segment.pointAt(segment.pointClosestTo(point).position());
          /*  if (pointClosestToOfSegment.distanceTo(point) < smallestDistance) {
                routePointClosestTo = segment.pointClosestTo(point).withPositionShiftedBy(shift);
                smallestDistance = pointClosestToOfSegment.distanceTo(point);
            }
           */

    /**
     * Computes the relative position of a point on a single route given its position on the
     * MultiRoute. Given a position on the multiRoute, which contains a list of routes,
     * it computes the position of the point on the segment it belongs to.
     *
     * @param indexOfSegment index of the segment point of given position belongs to.
     * @param position       position of the point on the route.
     * @return given the position of a point of given position on the route it belongs to.
     */
    private double computeRelativePositionOnSegment(int indexOfSegment, double position) {
        double sum = 0;
        for (int i = 0; i < indexOfSegment; i++) {
            sum += segments.get(i).length();
        }
        return position - sum;
    }

    /**
     * Returns the index of the road in the MultiRoute's list of roads, which contains the given position.
     *
     * @param position given position to compute the index of the route from.
     * @return the index of the road in the MultiRoute's list of roads, which contains the given position.
     */
    private int indexOfRoadAt(double position) {
        if (position == this.length()) return segments.size() - 1;
        int index = 0;
        double sum = 0;
        while (sum <= position) {
            sum += segments.get(index).length();
            index++;
        }
        return index - 1;
    }
}
