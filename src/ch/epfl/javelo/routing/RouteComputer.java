package ch.epfl.javelo.routing;

import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.data.Graph;
import java.util.*;

/**
 * represents the profile of a single or multiple itinerary
 *
 * @author Ambroise Aigueperse (341890)
 * @author Louis Roche (345620)
 */
final public class RouteComputer {
    /**
     * the graph from which the route will be created
     */
    private final Graph graph;
    /**
     * the costFunction used to calculate the shortest Route
     */
    private final CostFunction costFunction;

    /**
     * constructor of RouteComputer
     *
     * @param graph        the graph from which the route will be created
     * @param costFunction the costFunction used to calculate the shortest Route
     */
    public RouteComputer(Graph graph, CostFunction costFunction) {
        this.graph = graph;
        this.costFunction = costFunction;
    }

    /**
     * finds the shortest Route  between 2 nodes, weighted with the costfunction
     *
     * @param startNodeId id of the Route's starting node
     * @param endNodeId   id of the Route's ending node
     * @return the shortest Route  between 2 nodes, weighted with the costfunction
     * @throws IllegalArgumentException if the starting point is the same as the destination
     */
    public Route bestRouteBetween(int startNodeId, int endNodeId) {
        Preconditions.checkArgument(startNodeId != endNodeId);

        //record containing both the node identity and the super distance
        // (sum of distance found by Dijkstra and distance between the node and the endNode)
        record WeightedNode(int nodeId, float distance)
                implements Comparable<WeightedNode> {
            /**
             * @inheritDoc
             * @param that the WeightedNode that is compared to the current WeightedNode
             */
            @Override
            public int compareTo(WeightedNode that) {
                return Float.compare(this.distance, that.distance);
            }
        }

        float[] distances = new float[graph.nodeCount()];
        int[] predecesseur = new int[graph.nodeCount()];
        PriorityQueue<WeightedNode> en_exploration = new PriorityQueue<>();

        //initiate all distances to positive infinity
        for (int i = 0; i < distances.length; ++i) {
            predecesseur[i] = 0;
            distances[i] = Float.POSITIVE_INFINITY;
        }

        //distance of the starting node is 0 and it is added to en_exploration
        distances[startNodeId] = 0f;
        en_exploration.add(new WeightedNode(startNodeId, distances[startNodeId] + distanceToTarget(graph, startNodeId, endNodeId)));

        while (!en_exploration.isEmpty()) {
            //N = WeightedNode in en_exploration with minimal distance
            WeightedNode N = en_exploration.remove();
            //if its distance is NEGATIVE_INFINITY (meaning it has already been checked), we discard it
            //else we do the following
            if (distances[N.nodeId] != Float.NEGATIVE_INFINITY) {
                if (N.nodeId == endNodeId) return finalPath(predecesseur, startNodeId, endNodeId);
                float distanceN = distances[N.nodeId];
                //for each edge coming out of N
                for (int i = 0; i < graph.nodeOutDegree(N.nodeId); ++i) {
                    int edgeId = graph.nodeOutEdgeId(N.nodeId, i);
                    //Nbis = end node of the considered edge
                    WeightedNode Nbis = new WeightedNode(graph.edgeTargetNodeId(edgeId)
                            , distances[graph.edgeTargetNodeId(edgeId)] + distanceToTarget(graph, graph.edgeTargetNodeId(edgeId)
                            , endNodeId));
                    //calculate the potential new distance of Nbis
                    float d = distanceN + (float) (costFunction.costFactor(N.nodeId, edgeId) * graph.edgeLength(edgeId));
                    //update the different Arrays and queues if the new distance to Nbis smaller than the previous one
                    if (d < distances[Nbis.nodeId]) {
                        distances[Nbis.nodeId] = d;
                        predecesseur[Nbis.nodeId] = N.nodeId;
                        en_exploration.add(new WeightedNode(Nbis.nodeId, distances[Nbis.nodeId] + distanceToTarget(graph, graph.edgeTargetNodeId(edgeId), endNodeId)));
                    }
                }
                //specify that the node has been visited
                distances[N.nodeId] = Float.NEGATIVE_INFINITY;
            }
        }
        return null;
    }

    /**
     * reconstructs recursively the route starting form startNodeId and ending at endNodeId with given predecesseur list
     *
     * @param predecesseur list where at each index corresponding to the id of a node, the id of the previous node in the path is stored
     * @param startNodeId  id of the starting node of the route
     * @param endNodeId    id of the ending node of the route
     * @return the route starting form startNodeId and ending at endNodeId with given predecesseur list
     */
    private Route finalPath(int[] predecesseur, int startNodeId, int endNodeId) {
        List<Edge> edges = new ArrayList<>();
        int nodeId = endNodeId;
        int formerNodeId;
        //recursively get all the edges in the Route, starting from the last edge.
        //the previous edge in the route is got given its toNodeId and its fromNodeId (=predecesseur[toNodeId]
        do {
            formerNodeId = predecesseur[nodeId];
            for (int i = 0; i < graph.nodeOutDegree(formerNodeId); ++i) {
                int edgeId = graph.nodeOutEdgeId(formerNodeId, i);
                if (graph.edgeTargetNodeId(edgeId) == nodeId) {
                    edges.add(Edge.of(graph, edgeId, formerNodeId, nodeId));
                    nodeId = formerNodeId;
                    break;
                }
            }
        } while (formerNodeId != startNodeId);
        //reverse the list of edges because they were added recursively from the last one
        Collections.reverse(edges);
        return new SingleRoute(edges);
    }

    /**
     * returns the distance between the points corresponding to the nodes with given id
     *
     * @param graph        the graph containing the nodes
     * @param nodeId       id of the first node
     * @param targetNodeId id of the second node
     * @return the distance between the points corresponding to the nodes with given id
     */
    private float distanceToTarget(Graph graph, int nodeId, int targetNodeId) {
        return (float) graph.nodePoint(nodeId).distanceTo(graph.nodePoint(targetNodeId));
    }
}
