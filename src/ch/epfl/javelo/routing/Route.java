package ch.epfl.javelo.routing;

import ch.epfl.javelo.projection.PointCh;

import java.util.List;

/**
 * represents a route
 *
 * @author Ambroise Aigueperse (341890)
 * @author Louis Roche (345620)
 */

public interface Route {
    /**
     * returns the segment index at the given position (in meters)
     * @param position the given position
     * @return the segment index at the given position (in meters)
     */
    int indexOfSegmentAt(double position);

    /**
     * returns the length of the route (meters)
     * @return the length of the route (meters)
     */
    double length();

    /**
     * returns all the edges of the route
     * @return all the edges of the route
     */
    List<Edge> edges();

    /**
     * returns all the points located at the ends of the edges of the route
     * @return all the points located at the ends of the edges of the route
     */
    List<PointCh> points();

    /**
     * returns the point at the given position along the route
     * @return the point at the given position along the route
     */
    PointCh pointAt(double position);

    /**
     * returns the identity of the node belonging to the route and being closest to the given position
     * @param position the given position
     * @return the identity of the node belonging to the route and being closest to the given position
     */
    int nodeClosestTo(double position);

    /**
     * returns the point on the route that is closest to the given reference point
     * @param point the given reference point
     * @return the point on the route that is closest to the given reference point
     */
    RoutePoint pointClosestTo(PointCh point);
}
