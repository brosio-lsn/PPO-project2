package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.data.GraphNodes;
import ch.epfl.javelo.projection.PointCh;

import java.util.function.DoubleUnaryOperator;

/**
 * represents an edge of a route
 *
 * @author Louis ROCHE (345620)
 * @author Ambroise AIGUEPERSE (341890)
 */

//TODO demander ici aussi comment est donné le profile - ça n'importe pas
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
        //TODO ask, on aurait pu utiliser Graph.edgeTargetNodeId pour choper le point d arriver mais ca change R je pense - oui effectivement
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
    //TODO ask que faire si position negatif ou trop grand
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
