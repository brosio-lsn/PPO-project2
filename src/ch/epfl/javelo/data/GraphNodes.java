package ch.epfl.javelo.data;

import ch.epfl.javelo.Bits;
import ch.epfl.javelo.Q28_4;

import java.nio.IntBuffer;

/**
 * represents the set of nodes contained in the JaVelo graph
 *
 * @author Louis ROCHE (345620)
 * @author Ambroise AIGUEPERSE (341890)
 */

public record GraphNodes(IntBuffer buffer) {

    private static final int OFFSET_E = 0;
    private static final int OFFSET_N = OFFSET_E + 1;
    private static final int OFFSET_OUT_EDGES = OFFSET_N + 1;
    private static final int NODE_INTS = OFFSET_OUT_EDGES + 1;

    /**
     * returns the total number of nodes
     * @return the total number of nodes
     */
    public int count(){
        return  buffer.capacity()/NODE_INTS;
    }

    /**
     * returns the E coordinate of the node whose identity is given
     * @param nodeId the identity of the considered node
     * @return the E coordinate of the node whose identity is given
     */
    public double nodeE(int nodeId){
        return Q28_4.asDouble(buffer.get(nodeId*NODE_INTS + OFFSET_E));
        //TODO sur de consversion
    }

    /**
     * returns the N coordinate of the node whose identity is given
     * @param nodeId the identity of the considered node
     * @return the N coordinate of the node whose identity is given
     */
    public double nodeN(int nodeId){
        return Q28_4.asDouble(buffer.get(nodeId*NODE_INTS + OFFSET_N));
    }

    /**
     * returns the number of edges coming out of the node with given identity
      * @param nodeId the identity of the considered node
     * @return the number of edges coming out of the node with given identity
     */
    public double outDegree(int nodeId){
        //System.out.println(nodeId*NODE_INTS + OFFSET_OUT_EDGES);
        return Bits.extractUnsigned(buffer.get(nodeId*NODE_INTS + OFFSET_OUT_EDGES),28,4 );
    }

    /**
     * returns the identity of the edgeIndex-th edge coming out of the node with given identity
     * @param nodeId the identity of the considered node
     * @param edgeIndex the index of the edge in the list containing all the edges coming out of the node
     * @return the identity of the edgeIndex-th edge coming out of the node with given identity
     */
    public double edgeId(int nodeId, int edgeIndex){
        assert 0 <= edgeIndex && edgeIndex < outDegree(nodeId);
        return Bits.extractUnsigned(buffer.get(nodeId*NODE_INTS + OFFSET_OUT_EDGES),0,28 )+edgeIndex;
    }

}
