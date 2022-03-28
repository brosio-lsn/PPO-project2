package ch.epfl.javelo.routing;

import ch.epfl.javelo.projection.PointCh;

import static java.lang.Double.NaN;
import static java.lang.Double.POSITIVE_INFINITY;

/**
 * Represents a point on a route.
 * @author ROCHE Louis (345620)
 * @author AIGUEPERSE Ambroise (341890)
 */


/**
 * Default RoutePoint record constructor.
 *
 * @param point               (PointCh) Point in Switzerland represented by the RoutePoint
 * @param position            (double) position relative to the route.
 * @param distanceToReference (double) distance to the reference.
 */
public record RoutePoint(PointCh point, double position, double distanceToReference) {
    /**
     * represents an non-existent point
     */
    public static final RoutePoint NONE = new RoutePoint(null, NaN, POSITIVE_INFINITY);

    /**
     * returns a point identical to the current RoutePoint (this) but whose position is offset by the given difference (positive of negative)
     *
     * @param positionDifference the difference of position
     * @return a point identical to the current RoutePoint (this) but whose position is offset by the given difference (positive of negative)
     */
    public RoutePoint withPositionShiftedBy(double positionDifference) {
        return new RoutePoint(this.point, this.position + positionDifference, this.distanceToReference);
    }

    /**
     * returns the current RoutePoint if its distance to the reference is less than or equal to the given RoutePoint, and the given RoutePoint otherwise
     *
     * @param that the given RoutePoint
     * @return the current RoutePoint if its distance to the reference is less than or equal to the given RoutePoint, and the given RoutePoint otherwise
     */
    public RoutePoint min(RoutePoint that) {
        return this.distanceToReference <= that.distanceToReference ? this : that;
    }

    /**
     * returns the current RoutePointis if its distance to the reference is less than or equal to
     * thatDistanceToReference, and a new instance of RoutePoint whose attributes are the arguments passed to min otherwise
     *
     * @param thatPoint               the point on the route of the potential new RoutePoint
     * @param thatPosition            the position of the potential new RoutePoint  along the route
     * @param thatDistanceToReference the distance between the potential new RoutePoint and the reference
     * @return the current RoutePointis if its distance to the reference is less than or equal to
     * * thatDistanceToReference, and a new instance of RoutePoint whose attributes are the arguments passed to min otherwise
     */
    public RoutePoint min(PointCh thatPoint, double thatPosition, double thatDistanceToReference) {
        return this.distanceToReference <= thatDistanceToReference ? this : new RoutePoint(thatPoint, thatPosition, thatDistanceToReference);
    }
}
