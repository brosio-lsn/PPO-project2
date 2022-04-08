package ch.epfl.javelo.projection;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;

/**
 * represents a point in the Web Mercator system
 *
 * @author Louis ROCHE (345620)
 * @author Ambroise AIGUEPERSE (341890)
 */

public record PointWebMercator(double x, double y) {
    private static int ZOOM_LEVEL_0 = 8;
    /**
     * Constructor of the class
     *
     * @param x the x coordinate of the point
     * @param y the y coordinate of the point
     * @throws IllegalArgumentException if the x coordinate or the y coordinate are not in the interval [0,1]
     */
    public PointWebMercator {
        Preconditions.checkArgument(x == Math2.clamp(0, x, 1) && y == Math2.clamp(0, y, 1));
    }

    /**
     * returns the Web Mercator point of whose coordinates at the given zoom level are x and y
     *
     * @param zoomLevel the zoom level at which x and y are expressed
     * @param x         the x coordinate at the given zoom level
     * @param y         the y coordinate at the given zoom level
     * @return the Web Mercator point whose coordinates at the given zoom level are x and y
     */
    public static PointWebMercator of(int zoomLevel, double x, double y) {
        double newX = Math.scalb(x, -(ZOOM_LEVEL_0 + zoomLevel));
        double newY = Math.scalb(y, -(ZOOM_LEVEL_0 + zoomLevel));
        return new PointWebMercator(newX, newY);
    }

    /**
     * returns the Web Mercator point corresponding to the given point expressed in Swiss system
     *
     * @param pointCh the given point expressed in suiss system
     * @return the equivalent Web Mercator point
     */
    public static PointWebMercator ofPointCh(PointCh pointCh) {
        double x = WebMercator.x(pointCh.lon());
        double y = WebMercator.y(pointCh.lat());
        return new PointWebMercator(x, y);
    }


    /**
     * retruns the x coordinate of the point at the given zoom level
     *
     * @param zoomLevel the zoom level at which the coordinate will be returned
     * @return the x coordinate of the point at the given zoom level
     */
    public double xAtZoomLevel(int zoomLevel) {
        return Math.scalb(x, ZOOM_LEVEL_0 + zoomLevel);
    }

    /**
     * retruns the y coordinate of the point at the given zoom level
     *
     * @param zoomLevel the zoom level at which the coordinate will be returned
     * @return the y coordinate of the point at the given zoom level
     */
    public double yAtZoomLevel(int zoomLevel) {return Math.scalb(y, ZOOM_LEVEL_0 + zoomLevel);}

    /**
     * returns the longitude of the point
     *
     * @return the longitude of the point
     */
    public double lon() {
        return WebMercator.lon(this.x);
    }

    /**
     * returns the latitude of the point
     *
     * @return the latitude of the point
     */
    public double lat() {
        return WebMercator.lat(this.y);
    }

    /**
     * returns the PointCh point at the same position as the instance this or null if the point isn't within suiss bounds
     *
     * @return the PointCh point at the same position as the instance this or null if the point isn't within suiss bounds
     */
    public PointCh toPointCh() {
        double e = Ch1903.e(lon(), lat());
        double n = Ch1903.n(lon(), lat());
        return (SwissBounds.containsEN(e, n) ? new PointCh(e, n) : null);
    }
}
