package ch.epfl.javelo.routing;

import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;

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
     * @param graph the graph from which the route will be created
     * @param costFunction the costFunction used to calculate the shortest Route
     */
    public RouteComputer(Graph graph, CostFunction costFunction){
        this.graph=graph;
        this.costFunction=costFunction;
    }

    /**
     * finds the shortest Route  between 2 nodes, weighted with the costfunction
     * @param startNodeId id of the Route's starting node
     * @param endNodeId id of the Route's ending node
     * @return the shortest Route  between 2 nodes, weighted with the costfunction
     */
    public Route bestRouteBetween(int startNodeId, int endNodeId){
        //TODO DO NOT delete this, this is the first version of the algo
       /* Preconditions.checkArgument(startNodeId != endNodeId);
        record WeightedNode(int nodeId, float distance)
                implements Comparable<WeightedNode> {
            @Override
            public int compareTo(WeightedNode that) {
                return Float.compare(this.distance, that.distance);
            }
        }
        float [] distances = new float[graph.nodeCount()];
        int [] prédécésseurs  = new int[graph.nodeCount()];
        PriorityQueue<WeightedNode> en_exploration = new PriorityQueue<>();
        for(int i =0; i<distances.length;++i){
            prédécésseurs[i]=0;
            distances[i]= Float.POSITIVE_INFINITY;
        }
        distances[startNodeId]=0f;
        en_exploration.add(new WeightedNode(startNodeId, distances[startNodeId]));
        while(!en_exploration.isEmpty()) {
            WeightedNode N = en_exploration.remove();
            if (distances[N.nodeId] != Float.NEGATIVE_INFINITY) {
                if (N.nodeId == endNodeId) return finalPath(prédécésseurs, startNodeId, endNodeId);
                float distanceN=distances[N.nodeId];
                for (int i = 0; i < graph.nodeOutDegree(N.nodeId); ++i) {
                    int edgeId = graph.nodeOutEdgeId(N.nodeId, i);
                    WeightedNode Nbis = new WeightedNode(graph.edgeTargetNodeId(edgeId), distances[graph.edgeTargetNodeId(edgeId)]);
                    float d = distanceN + (float) (costFunction.costFactor(N.nodeId, edgeId)*graph.edgeLength(edgeId));//todo normal le transtipage?
                    if (d < distances[Nbis.nodeId]) {
                        distances[Nbis.nodeId] = d;
                        prédécésseurs[Nbis.nodeId] = N.nodeId;
                        float a  = distances[Nbis.nodeId];
                        en_exploration.add(new WeightedNode(Nbis.nodeId, distances[Nbis.nodeId]));
                    }
                    distances[N.nodeId] = Float.NEGATIVE_INFINITY;
                }
            }
        }*/
/*
         Preconditions.checkArgument(startNodeId != endNodeId);
        record WeightedNode(int nodeId, float distance)
                implements Comparable<WeightedNode> {
            @Override
            public int compareTo(WeightedNode that) {
                return Float.compare(this.distance, that.distance);
            }
        }
        float [] distances = new float[graph.nodeCount()];
        int [] prédécésseurs  = new int[graph.nodeCount()];
        PriorityQueue<WeightedNode> en_exploration = new PriorityQueue<>();
        for(int i =0; i<distances.length;++i){
            prédécésseurs[i]=0;
            distances[i]= Float.POSITIVE_INFINITY;
        }
        distances[startNodeId]=0f;
        en_exploration.add(new WeightedNode(startNodeId, distances[startNodeId]));
        while(!en_exploration.isEmpty()) {
            WeightedNode N = en_exploration.remove();
            if (distances[N.nodeId] != Float.NEGATIVE_INFINITY) {
                if (N.nodeId == endNodeId) return finalPath(prédécésseurs, startNodeId, endNodeId);
                float distanceN=distances[N.nodeId];
                for (int i = 0; i < graph.nodeOutDegree(N.nodeId); ++i) {
                    int edgeId = graph.nodeOutEdgeId(N.nodeId, i);
                    WeightedNode Nbis = new WeightedNode(graph.edgeTargetNodeId(edgeId), distances[graph.edgeTargetNodeId(edgeId)]);
                    float d = distanceN + (float) (costFunction.costFactor(N.nodeId, edgeId)*graph.edgeLength(edgeId)) + distanceToTarget(graph, N.nodeId, endNodeId);//todo normal le transtipage?
                    if (d < distances[Nbis.nodeId]) {
                        distances[Nbis.nodeId] = d;
                        prédécésseurs[Nbis.nodeId] = N.nodeId;
                        float a  = distances[Nbis.nodeId];
                        en_exploration.add(new WeightedNode(Nbis.nodeId, distances[Nbis.nodeId]));
                    }
                    distances[N.nodeId] = Float.NEGATIVE_INFINITY;
                }
            }
        }
        return null;*/
    /*//TODO DO NOT delete first A* version
        //TODO ask question for A*
        Preconditions.checkArgument(startNodeId != endNodeId);
        record WeightedNode(int nodeId, float distance, float distanceToTarget)
                implements Comparable<WeightedNode> {
            @Override
            public int compareTo(WeightedNode that) {
                return Float.compare(this.distance+this.distanceToTarget, that.distance+that.distanceToTarget);
            }
        }
        float [] distances = new float[graph.nodeCount()];
        int [] prédécésseurs  = new int[graph.nodeCount()];
        PriorityQueue<WeightedNode> en_exploration = new PriorityQueue<>();
        for(int i =0; i<distances.length;++i){
            prédécésseurs[i]=0;
            distances[i]= Float.POSITIVE_INFINITY;
        }
        distances[startNodeId]=0f;
        en_exploration.add(new WeightedNode(startNodeId, distances[startNodeId],distanceToTarget(graph, startNodeId, endNodeId)));
        while(!en_exploration.isEmpty()) {
            WeightedNode N = en_exploration.remove();
            if (distances[N.nodeId] != Float.NEGATIVE_INFINITY) {
                if (N.nodeId == endNodeId) return finalPath(prédécésseurs, startNodeId, endNodeId);
                float distanceN=distances[N.nodeId];
                for (int i = 0; i < graph.nodeOutDegree(N.nodeId); ++i) {
                    int edgeId = graph.nodeOutEdgeId(N.nodeId, i);
                    WeightedNode Nbis = new WeightedNode(graph.edgeTargetNodeId(edgeId), distances[graph.edgeTargetNodeId(edgeId)],distanceToTarget(graph, graph.edgeTargetNodeId(edgeId), endNodeId) );
                    float d = distanceN + (float) (costFunction.costFactor(N.nodeId, edgeId)*graph.edgeLength(edgeId)) ;//todo normal le transtipage?
                    if (d < distances[Nbis.nodeId]) {
                        distances[Nbis.nodeId] = d;
                        prédécésseurs[Nbis.nodeId] = N.nodeId;
                        float a  = distances[Nbis.nodeId];
                        en_exploration.add(new WeightedNode(Nbis.nodeId, distances[Nbis.nodeId], Nbis.distanceToTarget));
                    }
                    distances[N.nodeId] = Float.NEGATIVE_INFINITY;
                }
            }
        }
        return null;*/
        //TODO DO NOT delete FINAL A* version
        //TODO ask question for A*
        Preconditions.checkArgument(startNodeId != endNodeId);
        record WeightedNode(int nodeId, float distance)
                implements Comparable<WeightedNode> {
            @Override
            public int compareTo(WeightedNode that) {
                return Float.compare(this.distance, that.distance);
            }
        }
        float [] distances = new float[graph.nodeCount()];
        int [] prédécésseurs  = new int[graph.nodeCount()];
        PriorityQueue<WeightedNode> en_exploration = new PriorityQueue<>();
        for(int i =0; i<distances.length;++i){
            prédécésseurs[i]=0;
            distances[i]= Float.POSITIVE_INFINITY;
        }
        distances[startNodeId]=0f;
        en_exploration.add(new WeightedNode(startNodeId, distances[startNodeId]+distanceToTarget(graph, startNodeId, endNodeId)));
        while(!en_exploration.isEmpty()) {
            WeightedNode N = en_exploration.remove();
            if (distances[N.nodeId] != Float.NEGATIVE_INFINITY) {
                if (N.nodeId == endNodeId) return finalPath(prédécésseurs, startNodeId, endNodeId);
                float distanceN=distances[N.nodeId];
                for (int i = 0; i < graph.nodeOutDegree(N.nodeId); ++i) {
                    int edgeId = graph.nodeOutEdgeId(N.nodeId, i);
                    WeightedNode Nbis = new WeightedNode(graph.edgeTargetNodeId(edgeId), distances[graph.edgeTargetNodeId(edgeId)]+distanceToTarget(graph, graph.edgeTargetNodeId(edgeId), endNodeId));
                    float d = distanceN + (float) (costFunction.costFactor(N.nodeId, edgeId)*graph.edgeLength(edgeId)) ;
                    if (d < distances[Nbis.nodeId]) {
                        distances[Nbis.nodeId] = d;
                        prédécésseurs[Nbis.nodeId] = N.nodeId;
                        en_exploration.add(new WeightedNode(Nbis.nodeId, distances[Nbis.nodeId]+distanceToTarget(graph, graph.edgeTargetNodeId(edgeId), endNodeId)));
                    }
                    distances[N.nodeId] = Float.NEGATIVE_INFINITY;
                }
            }
        }
        return null;
    }

    /**
     * reconstructs the route starting form startNodeId and ending at endNodeId with given prédécésseur list
     * @param prédécésseur list where at each index corresponding to the id of a node, the id of the previous node in the path is stored
     * @param startNodeId id of the starting node of the route
     * @param endNodeId id of the ending node of the route
     * @return the route starting form startNodeId and ending at endNodeId with given prédécésseur list
     */
    private Route finalPath(int[] prédécésseur,int startNodeId, int endNodeId){
        List<Edge> edges = new ArrayList<>();
        int nodeId=endNodeId;
        int formerNodeId;
        do{
            formerNodeId= prédécésseur[nodeId];
            for(int i =0; i< graph.nodeOutDegree(formerNodeId);++i) {
                int edgeId = graph.nodeOutEdgeId(formerNodeId, i);
                if(graph.edgeTargetNodeId(edgeId)==nodeId){
                    edges.add(Edge.of(graph, edgeId, formerNodeId, nodeId));
                    nodeId=formerNodeId;
                    break;
                }
            }
        } while(formerNodeId != startNodeId);
        Collections.reverse(edges);
        return new SingleRoute(edges);
    }

    /**
     * returns the distance between the points corresponding to the nodes with given id
     * @param graph the graph containing the nodes
     * @param nodeId id of the first node
     * @param targetNodeId id of the second node
     * @return the distance between the points corresponding to the nodes with given id
     */
    private float distanceToTarget (Graph graph, int nodeId, int targetNodeId){
        return (float) graph.nodePoint(nodeId).distanceTo(graph.nodePoint(targetNodeId));
    }
}
