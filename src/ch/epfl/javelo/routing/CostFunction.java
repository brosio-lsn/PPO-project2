package ch.epfl.javelo.routing;
/**
 * represents a cost function
 * @author Louis ROCHE (345620)
 * @author Ambroise AIGUEPERSE (341890)
 */
public interface CostFunction {
    /**
     * returns the factor by which the length of the edge with identity edgeId, starting from the node with given identity,
     * must be multiplied; this factor must be greater than or equal to 1
     * @param nodeId node from which the edge comes out
     * @param edgeId the identity of the edge
     * @return the factor by which the length of the edge with identity edgeId, starting from the node with given identity,
     *      * must be multiplied
     */
    double costFactor(int nodeId, int edgeId);
}
