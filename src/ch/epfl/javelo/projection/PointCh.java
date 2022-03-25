package ch.epfl.javelo.projection;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;

import static ch.epfl.javelo.projection.SwissBounds.*;

/**
 * @author Louis ROCHE (345620)
 * @author Ambroise AIGUEPERSE (341890)
 */

public record PointCh(double e, double n) {
    /**
     * Constructor of the class PointCh
     *
     * @param e east coordinate of the PointCh
     * @param n north coordinate of the PointCh
     * @throws IllegalArgumentException if PointCh is not within the bounds of Switzerland, defined by
     *                                  MIN_E, MAX_E, MIN_N, MAX_N.
     */
    public PointCh {
        Preconditions.checkArgument(SwissBounds.containsEN(e, n));
    }

    /**
     * Returns the squared distance from this PointCh to that PointCh.
     *
     * @param that Point we desire to compute the squared distance to.
     * @return the squared distance from this to that, which is the squared norm of the vector
     * that goes from this PointCh to that PointCh.
     */
    public double squaredDistanceTo(PointCh that) {
        return (Math2.squaredNorm((that.e - this.e), (that.n - this.n)));
    }

    /**
     * Returns the distance from this PointCh to that PointCh.
     *
     * @param that, point which we desire to compute the distance to.
     * @return the distance from this PointCh to that PointCh, which is the square root of its squaredDistance
     */
    public double distanceTo(PointCh that) {
        return (Math2.norm(that.e - this.e, that.n - this.n));
    }

    /**
     * returns the latitude in radians of this PointCh.
     *
     * @return the latitude in radians of this. Uses the Ch1903 class for the computations.
     */
    public double lat() {
        return Ch1903.lat(this.e, this.n);
    }

    /**
     * returns the longitude in radians of this PointCh.
     *
     * @return the longitude in radians of this. Uses the Ch1903 class for the computations.
     */
    public double lon() {
        return Ch1903.lon(this.e, this.n);
    }
}
