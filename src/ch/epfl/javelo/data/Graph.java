package ch.epfl.javelo.data;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.projection.PointCh;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleUnaryOperator;

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

    /**
     * returns a new graph loaded from the files at the given basePath
     *
     * @param basePath path to read the files from
     * @return a new graph loaded from the files at the given basePath.
     * @throws IOException if the files do not exist.
     */
    public static Graph loadFrom(Path basePath) throws IOException {
        Path nodesPath = basePath.resolve("nodes.bin");
        Path edgesPath = basePath.resolve("edges.bin");
        Path sectorsPath = basePath.resolve("sectors.bin");
        Path attributesPath = basePath.resolve("attributes.bin");
        Path profile_idsPath = basePath.resolve("profile_ids.bin");
        Path elevationsPath = basePath.resolve("elevations.bin");
        IntBuffer nodesBuffer;
        ByteBuffer edgesBuffer;
        IntBuffer profileIds;
        ShortBuffer elevations;
        ByteBuffer sectorsBuffer;
        LongBuffer attributesSet;
        List<AttributeSet> attributeSetList;
        try (FileChannel edgesChannel  = FileChannel.open(edgesPath);
        FileChannel nodesChannel = FileChannel.open(nodesPath);
        FileChannel sectorsChannel = FileChannel.open(sectorsPath);
        FileChannel attributesChannel = FileChannel.open(attributesPath);
        FileChannel profile_idsChannel = FileChannel.open(profile_idsPath);
        FileChannel elevationsChannel = FileChannel.open(elevationsPath)){
             edgesBuffer = edgesChannel.map(FileChannel.MapMode.READ_ONLY, 0, edgesChannel.size());
             nodesBuffer = nodesChannel.map(FileChannel.MapMode.READ_ONLY, 0, nodesChannel.size()).asIntBuffer();
             profileIds = profile_idsChannel.map(FileChannel.MapMode.READ_ONLY, 0, profile_idsChannel.size())
                     .asIntBuffer();
             elevations = elevationsChannel.map(FileChannel.MapMode.READ_ONLY, 0, elevationsChannel.size())
                     .asShortBuffer();
             sectorsBuffer = sectorsChannel.map(FileChannel.MapMode.READ_ONLY, 0, sectorsChannel.size());
             attributesSet = attributesChannel.map(FileChannel.MapMode.READ_ONLY, 0, attributesChannel.size())
                     .asLongBuffer();
        }
        List<AttributeSet> attributs = new ArrayList<AttributeSet>();
        for (int i = 0; i < attributesSet.capacity(); i++) {
            attributs.add(new AttributeSet(attributesSet.get(i)));
        }
        return new Graph(new GraphNodes(nodesBuffer), new GraphSectors(sectorsBuffer), new GraphEdges(edgesBuffer,
                profileIds, elevations), attributs);
    }

    /**
     * returns the number of nodes the graph has
     *
     * @return (int) the number of nodes the graph has.
     */
    public int nodeCount() {
        return this.nodes.count();
    }

    /**
     * returns the PointCh corresponding to the node at the given id.
     *
     * @param nodeId id of the node, which will permit the program to identify the node to transform.
     * @return the PointCh corresponding to the node at the give id.
     */
    public PointCh nodePoint(int nodeId) {
        return new PointCh(nodes.nodeE(nodeId), nodes.nodeN(nodeId));
    }

    /**
     *
     * @param nodeId
     * @return
     */
    public int nodeOutDegree(int nodeId) {
        return nodes.outDegree(nodeId);
    }
    public int nodeOutEdgeId(int nodeId, int edgeIndex) {
        return nodes.edgeId(nodeId, edgeIndex);
    }
    public int nodeClosestTo(PointCh point, double searchDistance) {
        List<GraphSectors.Sector> sectorsNearby = sectors.sectorsInArea(point, searchDistance);
        List<PointCh> candidates = new ArrayList<>();
        List<Integer> indexLists = new ArrayList<>();
        for(GraphSectors.Sector secteurs : sectorsNearby) {
            for (int i = secteurs.startNodeId(); i <= secteurs.endNodeId(); ++i) {
                candidates.add(nodePoint(i));
                indexLists.add(i);
            }
        }
        if (candidates.isEmpty()) return -1;
        int index = 0;
        double minDistance = Double.POSITIVE_INFINITY;
        for (int i = 0; i<candidates.size(); i++) {
            if (candidates.get(i).squaredDistanceTo(point) < minDistance) {
                index = indexLists.get(i);
                minDistance = candidates.get(i).squaredDistanceTo(point);
            }
        }
        return index;
    }
    public int edgeTargetNodeId(int edgeId) {
        return edges.targetNodeId(edgeId);
    }
    public boolean edgeIsInverted(int edgeId) {
        return edges.isInverted(edgeId);
    }
    public AttributeSet edgeAttributes(int edgeId) {
        return new AttributeSet(edges.attributesIndex(edgeId));
    }
    public double edgeLength(int edgeId) {
        return edges.length(edgeId);
    }
    public double elevationGain(int edgeId) {
        return edges.elevationGain(edgeId);
    }
    public DoubleUnaryOperator edgeProfile(int edgeId) {
        return (edges.hasProfile(edgeId) ? Functions.sampled(edges.profileSamples(edgeId), edgeLength(edgeId))
                : Functions.constant(Double.NaN));
    }
}
