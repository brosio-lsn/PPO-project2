package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointWebMercator;
import javafx.geometry.Point2D;

public record MapViewParameters(int zoomLevel, double x, double y) {
    //TODO ask si je dois bien donner des coordonnées Web Mercator
    public Point2D topLeft(){return new Point2D(x,y);}

    public MapViewParameters withMinXY (double x, double y){return new MapViewParameters(zoomLevel, x, y);}

    public PointWebMercator pointAt(double x, double y){return PointWebMercator.of(zoomLevel, this.x+x, this.y+y);}

    public double viewX(PointWebMercator point){
        return x-point.xAtZoomLevel(zoomLevel);
    }

    public double viewY(PointWebMercator point){
        return y-point.yAtZoomLevel(zoomLevel);
    }
}
