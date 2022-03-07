package ch.epfl.javelo.data;

import ch.epfl.javelo.projection.PointCh;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public final class Graph {
    private final GraphNodes nodes;
    private final GraphSectors sectors;
    private final GraphEdges edges;
    private final List<AttributeSet> attributeSets;
    public Graph(GraphNodes nodes, GraphSectors sectors, GraphEdges edges, List<AttributeSet> attributeSets) {
        this.attributeSets = attributeSets;
        this.edges = edges;
        this.nodes = nodes;
        this.sectors = sectors;
    }
    public static Graph loadFrom(Path basePath) throws IOException {

    }
    public int nodeCount() {
        return this.nodes.count();
    }
    public PointCh nodePoint(int nodeId) {
        return new PointCh(nodes.nodeE(nodeId), nodes.nodeN(nodeId));
    }

}
