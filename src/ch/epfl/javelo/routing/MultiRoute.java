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
    /**
     * the list of routes engendering a MultiRoute
     */
    private final List<Route> segments;

    /**
     * Constructs a MultiRoute, which is a concatenation of routes, given a list of Routes.
     * @param segments the list of Routes used to construct a MultiRoute, not empty.
     * @throws IllegalArgumentException if the list is empty.
     */
    public MultiRoute(List<Route> segments) {
        Preconditions.checkArgument(!segments.isEmpty());
        this.segments = List.copyOf(segments);
    }

    /**
     * {@inheritDoc}
     *
     * @param position position in the route, which belongs to the segment returned.
     * @return the index of the segment at a given position.
     */
    @Override
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
     *{@inheritDoc}
     *
     * @return the total length of the itinerary, by summing up the length of all its edges.
     */
    @Override
    public double length() {
        double longueur = 0;
        for (Route route : segments) {
            longueur += route.length();
        }
        return longueur;
    }

    /**
     * {@inheritDoc}
     *
     * @return the totality of the itinerary's edges.
     */
    @Override
    public List<Edge> edges() {
        List<Edge> edges = new ArrayList<>();
        for (Route route : segments) edges.addAll(route.edges());
        return edges;
    }

    /**
     * {@inheritDoc}
     * @return the totality of the itinerary's points.
     */
    @Override
    public List<PointCh> points() {
        List<PointCh> pointRoute = new ArrayList<>();
        for (Edge edge : edges()) {
            pointRoute.add(edge.fromPoint());
        }
        pointRoute.add(edges().get(edges().size() - 1).toPoint());
        return pointRoute;
    }

    /**
     * {@inheritDoc}
     *
     * @param position position to search the PointCh at.
     * @return the PointCh at the given position along the itinerary.
     */
    @Override
    public PointCh pointAt(double position) {
        double realPos = Math2.clamp(0, position, length());
        int index = indexOfRoadAt(realPos);
        double relativePosition = computeRelativePositionOnSegment(index, realPos);
        return segments.get(index).pointAt(relativePosition);
    }

    /**
     * {@inheritDoc}
     *
     * @param position position along the itinerary to get the elevation from.
     * @return the elevation of the itinerary at the given position.
     */
    @Override
    public double elevationAt(double position) {
        double realPos = Math2.clamp(0, position, length());
        int index = indexOfRoadAt(realPos);
        double relativePosition = computeRelativePositionOnSegment(index, realPos);
        return segments.get(index).elevationAt(relativePosition);
    }


    /**
     * {@inheritDoc}
     *
     * @param position position to search the closest node to.
     * @return the identity of the node closest to a given position along the itinerary.
     */
    @Override
    public int nodeClosestTo(double position) {
        double realPos = Math2.clamp(0, position, length());
        int indexOfRoad = indexOfRoadAt(realPos);
        double relativePosition = computeRelativePositionOnSegment(indexOfRoad, realPos);
        return segments.get(indexOfRoad).nodeClosestTo(relativePosition);
    }

    /**
     * {@inheritDoc}
     *
     * @param point given point to find its closest RoutePoint on the itinerary.
     * @return the closest RoutePoint to a given point on the itinerary.
     */
    @Override
    public RoutePoint pointClosestTo(PointCh point) {
        RoutePoint routePointClosestTo = RoutePoint.NONE;
        double shift = 0;
        for (Route segment : segments) {
                    routePointClosestTo = routePointClosestTo.min(segment.pointClosestTo(point).withPositionShiftedBy(shift));
            shift += segment.length();
        }
        return routePointClosestTo;
    }


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
