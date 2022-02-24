package ch.epfl.javelo.projection;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;

public record PointMercator(double x, double y) {
    public PointMercator{
        Preconditions.checkArgument(x== Math2.clamp(0,x,1) && y== Math2.clamp(0,x,1));
    }

    public static PointMercator of(int zoomLevel, double x, double y){
        return new PointMercator(Math.scalb(x,-(8+zoomLevel)), Math.scalb(x,(8+zoomLevel)));
    }

    public static PointMercator ofPointCh(PointCh pointCh){
        return new PointMercator(WebMercator.x(pointCh.lon()), WebMercator.y(pointCh.lat()));
    }

    public double xAtZoomLevel(int zoomLevel){
        return Math.scalb(x,8+zoomLevel);
    }

    public double yAtZoomLevel(int zoomLevel){
        return Math.scalb(y,8+zoomLevel);
    }

    public double lon(){
        return WebMercator.lon(this.x);
    }

    public double lat(){
        return WebMercator.lat(this.y);
    }

    public PointCh toPointCh(){
        PointCh pointCh=new PointCh(Ch1903.e(WebMercator.lon(this.x), WebMercator.lat(this.y)), Ch1903.n(WebMercator.lon(this.x), WebMercator.lat(this.y)));
        return (SwissBounds.containsEN(pointCh.e(), pointCh.n())? pointCh : null);
    }
}
