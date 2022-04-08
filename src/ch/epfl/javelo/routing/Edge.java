package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.data.GraphNodes;
import ch.epfl.javelo.projection.PointCh;

import java.util.function.DoubleUnaryOperator;

/**
 * Constructor of an Edge.
 *
 * @param fromNodeId (int) id of the node the edge originates from.
 * @param toNodeId   (int) id of the node the edge goes to.
 * @param fromPoint  (PointCh) point in Switzerland, corresponding to a node in the graph, the edge originates
 *                   from. Not Null.
 * @param toPoint    (PointCh) point in Switzerland, corresponding to a node in the graph, the edges goes
 *                   to. Not Null.
 * @param length     (double) length of the edge.
 * @param profile    (DoubleUnaryOperator) function that computs the approximate height at any given point
 *                   on the edge.
 */
public record Edge(int fromNodeId, int toNodeId, PointCh fromPoint, PointCh toPoint, double length,
                   DoubleUnaryOperator profile) {

    /**
     * constructs an edge with given parameters
     *
     * @param graph      the graph containing the new edge
     * @param edgeId     the identity of the new edge
     * @param fromNodeId the identity of the node at the start of the edge
     * @param toNodeId   the identity of the node at the end of the edge
     * @return the edge with given parameters
     */
    public static Edge of(Graph graph, int edgeId, int fromNodeId, int toNodeId) {
        return new Edge(fromNodeId, toNodeId, graph.nodePoint(fromNodeId), graph.nodePoint(toNodeId), graph.edgeLength(edgeId), graph.edgeProfile(edgeId));
    }

    /**
     * returns the position along the edge, in meters, that is closest to the given point
     *
     * @param point the given point
     * @return the position along the edge, in meters, that is closest to the given point,
     */
    public double positionClosestTo(PointCh point) {
        return Math2.projectionLength(fromPoint.e(), fromPoint.n(), toPoint.e(), toPoint.n(), point.e(), point.n());
    }

    /**
     * returns the point at the given position on the edge, in meters
     *
     * @param position the given position
     * @return the point at the given position on the edge, in meters
     */
    public PointCh pointAt(double position) {
        double e = position / length * (toPoint.e() - fromPoint.e()) + fromPoint.e();
        double n = position / length * (toPoint.n() - fromPoint.n()) + fromPoint.n();
        return new PointCh(e, n);
    }

    /**
     * returns the altitude, in meters, at the given position on the edge
     *
     * @param position the given position
     * @return the altitude, in meters, at the given position on the edge
     */
    public double elevationAt(double position) {
        return profile.applyAsDouble(position);
    }
}
