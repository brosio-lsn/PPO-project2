package ch.epfl.javelo.projection;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;

/**
 * @author Louis ROCHE (345620)
 * @author Ambroise AIGUEPERSE (341860)
 */

public record PointMercator(double x, double y) {
    /**
     * Constructor of the class
     *
     * @param x the x coordinate of the point
     * @param y the y coordinate of the point
     * @throws IllegalArgumentException if the x coordinate or the y coordinate are not in the interval [0,1]
     */
    public PointMercator {
        Preconditions.checkArgument(x == Math2.clamp(0, x, 1) && y == Math2.clamp(0, x, 1));
    }

    public String getX() {
        return (x + ":" + y);
    }

    /**
     * returns the Web Mercator point of whose coordinates at the given zoom level are x and y
     *
     * @param zoomLevel the zoom level at which x and y are expressed
     * @param x         the x coordinate at the given lzoom level
     * @param y         the y coordinate at the given lzoom level
     * @return the Web Mercator point whose coordinates at the given zoom level are x and y
     */
    public static PointMercator of(int zoomLevel, double x, double y) {
        return new PointMercator(Math.scalb(x, -(8 + zoomLevel)), Math.scalb(x, (8 + zoomLevel)));
    }

    /**
     * returns the Web Mercator point corresponding to the given point expressed in Swiss system
     *
     * @param pointCh the given point expressed in suiss system
     * @return the equivalent Web Mercator point
     */
    public static PointMercator ofPointCh(PointCh pointCh) {
        return new PointMercator(WebMercator.x(pointCh.lon()), WebMercator.y(pointCh.lat()));
    }

    /**
     * retruns the x coordinate of the point at the given zoom level
     *
     * @param zoomLevel the zoom level at which the coordinate will be returned
     * @return the x coordinate of the point at the given zoom level
     */
    public double xAtZoomLevel(int zoomLevel) {
        return Math.scalb(x, 8 + zoomLevel);
    }

    /**
     * retruns the y coordinate of the point at the given zoom level
     *
     * @param zoomLevel the zoom level at which the coordinate will be returned
     * @return the y coordinate of the point at the given zoom level
     */
    public double yAtZoomLevel(int zoomLevel) {
        return Math.scalb(y, 8 + zoomLevel);
    }

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
        PointCh pointCh = new PointCh(Ch1903.e(WebMercator.lon(this.x), WebMercator.lat(this.y)), Ch1903.n(WebMercator.lon(this.x), WebMercator.lat(this.y)));
        return (SwissBounds.containsEN(pointCh.e(), pointCh.n()) ? pointCh : null);
    }
}
