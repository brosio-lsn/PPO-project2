package ch.epfl.javelo.projection;

import ch.epfl.javelo.Math2;

/**
 * convert between WGS 84 and Web Mercator coordinates
 *
 * @author Ambroise Aigueperse (341890)
 * @author Louis Roche (345620)
 */

public final class WebMercator {

    /**
     * private constructor to make the class non instantiable
     */
    private WebMercator(){}

    /**
     * returns the x-coordinate of the projection of a
     * point lying at longitude lon, given in radians
     * @param lon the longitude of the point given in radians
     * @return the x-coordinate of the projection
     */
    public static double x(double lon){return 1/(2*Math.PI)*(lon+Math.PI);}

    /**
     * returns the longitude of the projection of a
     * point lying at longitude lon, given in radians
     * @param lat the longitude of the point given in radians
     * @return the y-coordinate of the projection
     */

    public static double y(double lat){return 1/(2*Math.PI)*(Math.PI- Math2.asinh(Math.tan(lat)));}

    /**
     * returns the longitude of the projection of a
     * point given its x projection coordinate
     * @param x the longitude of the point given in radians
     * @return the longitude of the point
     */
    public static double lon(double x){return (2*Math.PI*x-Math.PI);}

    /**
     * returns the latitude of the projection of a
     * point given its y projection coordinate
     * @param y the longitude of the point given in radians
     * @return the latitude of the point
     */
    public static double lat(double y){return (Math.atan(Math.sinh(Math.PI-2*Math.PI*y)));}
}
