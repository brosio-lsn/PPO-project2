package ch.epfl.javelo.routing;

import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.projection.PointCh;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;



final public class SingleRoute implements Route{
    private final List<Edge> edges;
    private final double[] nodesDistanceTable;

    public SingleRoute (List<Edge> edges){
        Preconditions.checkArgument(!edges.isEmpty());
        this.edges=List.copyOf(edges);
        nodesDistanceTable=this.createNodesDistanceTable();
    }

    @Override
    public int indexOfSegmentAt(double position) {
        return 0;
    }

    @Override
    public double length() {
        double length=0;
        for(Edge e : edges) length+=e.length();
        return length;
    }

    @Override
    public List<Edge> edges() {
        return edges;
    }

    @Override
    public List<PointCh> points() {
        List<PointCh> points= new ArrayList<PointCh>();
        for(Edge e : edges){
            points.add(e.fromPoint());
            points.add(e.toPoint());
        }
        return points;
    }

    @Override
    public PointCh pointAt(double position) {
        return null;
    }

    @Override
    public int nodeClosestTo(double position) {
        return 0;
    }

    @Override
    public RoutePoint pointClosestTo(PointCh point) {
        return null;
    }

    @Override
    public double elevationAt(double position) {
        return 0;
    }

    private double[] createNodesDistanceTable(){
        double[] nodesDistanceTable= new double[edges.size()+1];
        double lengthSum=0;
        for(int i=0; i<edges.size();++i){
            lengthSum+=edges.get(i).length();
            nodesDistanceTable[i+1]=lengthSum;
        };
        return nodesDistanceTable;
    }
}
