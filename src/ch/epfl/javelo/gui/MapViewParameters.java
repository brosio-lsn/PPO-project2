package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointWebMercator;
import javafx.geometry.Point2D;
/**
 * represents the parameters of the background map
 * presented in the graphical interface
 *
 * @author Louis ROCHE (345620)
 * @author Ambroise AIGUEPERSE (341890)
 */

/**
 * @param zoomLevel the zoom level
 * @param x x-coordiante of the top left corner
 * @param y y-coodrinate of the top left corner
 */
public record MapViewParameters(int zoomLevel, double x, double y) {

    /**
     * returns the coordinates of the top-left corner as a Point2D object
     * @return the coordinates of the top-left corner as a Point2D object
     */
    public Point2D topLeft(){return new Point2D(x,y);}

    /**
     * returns a MapViewParameters with same zoomLeve, but given x and y coordinates of the top left corner
     * @param x the new x coordinate of the top left corner
     * @param y the new y coordinate of the top left corner
     * @return a MapViewParameters with same zoomLeve, but given x and y coordinates of the top left corner
     */
    public MapViewParameters withMinXY (double x, double y){return new MapViewParameters(zoomLevel, x, y);}

    /**
     * returns the PointWebMercator whose coordinates are given
     * expressed relatively to the top left corner of the current MapViewParameters
     * @param x the x coordinate of the PointWebMercator expressed relatively
     *          to the top left corner of the current MapViewParameters
     * @param y the y coordinate of the PointWebMercator expressed relatively
     *           to the top left corner of the current MapViewParameters
     * @return the PointWebMercator whose coordinates are given
     *         expressed relatively to the top left corner of the current MapViewParameters
     */
    public PointWebMercator pointAt(double x, double y){return PointWebMercator.of(zoomLevel, this.x+x, this.y+y);}

    /**
     * returns the x coordinate of the given point, expressed relatively
     * to the top left corner of the current MapViewParameters
     * @param point the given point
     * @return the x coordinate of the given point, expressed relatively
     *         to the top left corner of the current MapViewParameters
     */
    public double viewX(PointWebMercator point){
        return point.xAtZoomLevel(zoomLevel)-x;
    }

    /**
     * returns the y coordinate of the given point, expressed relatively
     * to the top left corner of the current MapViewParameters
     * @param point the given point
     * @return the y coordinate of the given point, expressed relatively
     *         to the top left corner of the current MapViewParameters
     */
    public double viewY(PointWebMercator point){
        return point.yAtZoomLevel(zoomLevel)-y;
    }
}
