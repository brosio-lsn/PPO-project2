package ch.epfl.javelo.routing;

import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.data.Graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.PriorityQueue;

final public class RouteComputer {

    private final Graph graph;
    private final CostFunction costFunction;
    public RouteComputer(Graph graph, CostFunction costFunction){
        this.graph=graph;
        //TODO demander si immubale
        this.costFunction=costFunction;
    }

    public Route bestRouteBetween(int startNodeId, int endNodeId){
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
        distances[startNodeId]=0;
        en_exploration.add(new WeightedNode(startNodeId, distances[startNodeId]));
        while(!en_exploration.isEmpty()){
            WeightedNode N = en_exploration.remove();
            if(N.nodeId==endNodeId) return finalPath(prédécésseurs, startNodeId, endNodeId); //TODO finish
            for(int i =0; i< graph.nodeOutDegree(N.nodeId);++i){
                int edgeId = graph.nodeOutEdgeId(N.nodeId, i);
                WeightedNode Nbis = new WeightedNode(graph.edgeTargetNodeId(edgeId), distances[graph.edgeTargetNodeId(edgeId)]);
                float d = distances[N.nodeId]+ (float)graph.edgeLength(edgeId);//todo normal le transtipage?
                if(d<distances[Nbis.nodeId]) {
                    distances[Nbis.nodeId]=d;
                    prédécésseurs[Nbis.nodeId]=N.nodeId;
                    en_exploration.add(new WeightedNode(Nbis.nodeId, distances[Nbis.nodeId]));
                }
            }
        }
        return null;
    }

    private Route finalPath(int[] prédécésseur,int startNodeId, int endNodeId){
        ArrayList<Edge> edges = new ArrayList<>();
        int index=endNodeId;
        do{
            index= prédécésseur[index];
            edges.add()
        }
        return new SingleRoute();
    }
}
