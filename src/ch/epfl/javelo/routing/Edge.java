package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.data.GraphNodes;
import ch.epfl.javelo.projection.PointCh;

import java.util.function.DoubleUnaryOperator;

public record Edge(int fromNodeId, int toNodeId, PointCh fromPoint, PointCh toPoint, double length, DoubleUnaryOperator profile) {

    public static Edge of (Graph graph, int edgeId, int fromNodeId, int toNodeId){
        return new Edge(fromNodeId, toNodeId, graph.nodePoint(fromNodeId), graph.nodePoint(toNodeId), graph.edgeLength(edgeId), graph.edgeProfile(edgeId));
    }

    public double positionClosestTo(PointCh point){
        return Math2.projectionLength(fromPoint.e(), fromPoint.n(), point.e(), point.n(), toPoint.e(), toPoint.n());
    }
}
