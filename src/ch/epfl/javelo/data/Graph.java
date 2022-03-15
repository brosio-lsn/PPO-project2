package ch.epfl.javelo.data;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.projection.PointCh;

import java.io.IOException;
import java.nio.*;
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
        this.attributeSets = List.copyOf(attributeSets);
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
        ByteBuffer edgesBuffer = readFile(basePath, "edges.bin");
        IntBuffer nodesBuffer = readFile(basePath, "nodes.bin").asIntBuffer();
        IntBuffer profileIds = readFile(basePath, "profile_ids.bin").asIntBuffer();
        ShortBuffer elevations = readFile(basePath, "elevations.bin").asShortBuffer();
        ByteBuffer sectorsBuffer = readFile(basePath, "sectors.bin");
        LongBuffer attributesSet = readFile(basePath, "attributes.bin").asLongBuffer();
        List<AttributeSet> attributs = new ArrayList<>();
        for (int i = 0; i < attributesSet.capacity(); i++) {
            attributs.add(new AttributeSet(attributesSet.get(i)));
        }
        return new Graph(new GraphNodes(nodesBuffer), new GraphSectors(sectorsBuffer), new GraphEdges(edgesBuffer,
                profileIds, elevations), attributs);
    }

    /**
     * Opens a file channel from the base path and resolves it to a given file.
     * Returns a byteBuffer of the mapped given file.
     *
     * @param basePath Path the program has to go to in order to find the desired files.
     * @param fileName name of the file to map.
     * @return Returns a ByteBuffer of the mapped file.
     * @throws IOException if the file does not exist.
     */
    private static ByteBuffer readFile(Path basePath, String fileName) throws IOException {
        try (FileChannel channel = FileChannel.open(basePath.resolve(fileName))) {
            return channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
        }
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
     * @param nodeId (int) id of the node, which will permit the program to identify the node to transform.
     * @return (PointCh) the PointCh corresponding to the node at the give id.
     */
    public PointCh nodePoint(int nodeId) {
        return new PointCh(nodes.nodeE(nodeId), nodes.nodeN(nodeId));
    }

    /**
     * returns the number of edges coming out of a given node.
     *
     * @param nodeId (int) id of the node to extract the number of edges coming out from.
     * @return (int) the number of edges coming out of the given node at nodeId.
     */
    public int nodeOutDegree(int nodeId) {
        return nodes.outDegree(nodeId);
    }

    /**
     * return the ID of the edge #edgeIndex coming out of the given node.
     *
     * @param nodeId    (int) ID of the node to extract the ID of the edge #edgeIndex coming out of it.
     * @param edgeIndex (int) position of the edge in the list of the edges coming out of the given node.
     * @return (int) the ID of the edge #edgeIndex coming out of the given node.
     */
    public int nodeOutEdgeId(int nodeId, int edgeIndex) {
        return nodes.edgeId(nodeId, edgeIndex);
    }

    /**
     * returns the identity of the node closest to a given point, and given a radius of search.
     *
     * @param point          (PointCh) point to search the closest node to.
     * @param searchDistance (double) radius of the circle at which the node is to be searched
     * @return (int) the identity of the node closest to a given point, and given a radius of search.
     */
    public int nodeClosestTo(PointCh point, double searchDistance) {
        List<GraphSectors.Sector> sectorsNearby = sectors.sectorsInArea(point, searchDistance);
        List<PointCh> candidates = new ArrayList<>();
        List<Integer> indexLists = new ArrayList<>();
        for (GraphSectors.Sector secteurs : sectorsNearby) {
            for (int i = secteurs.startNodeId(); i <= secteurs.endNodeId(); ++i) {
                candidates.add(nodePoint(i));
                indexLists.add(i);
            }
        }
        int index = Integer.MAX_VALUE;
        double minDistance = Math.pow(searchDistance, 2);
        for (int i = 0; i < candidates.size(); i++) {
            if (candidates.get(i).squaredDistanceTo(point) < minDistance) {
                index = indexLists.get(i);
                minDistance = candidates.get(i).squaredDistanceTo(point);
            }
        }
        return (index == Integer.MAX_VALUE ? -1 : index);
    }

    /**
     * returns the ID of the target node of a given edge.
     *
     * @param edgeId (int) ID of the given edge
     * @return (int) ID of the target node of a given edge.
     */
    public int edgeTargetNodeId(int edgeId) {
        return edges.targetNodeId(edgeId);
    }

    /**
     * returns whether a given edge is inverted.
     *
     * @param edgeId (int) ID of the given edge.
     * @return true if the edge is inverted, false otherwise.
     */
    public boolean edgeIsInverted(int edgeId) {
        return edges.isInverted(edgeId);
    }

    /**
     * returns the OSM AttributeSet of a given edge.
     *
     * @param edgeId (int) ID of the given edge.
     * @return the OSM AttributeSet of a given edge. Not Null.
     */
    public AttributeSet edgeAttributes(int edgeId) {
        return attributeSets.get(edges.attributesIndex(edgeId));
    }

    /**
     * returns the length of a given edge.
     *
     * @param edgeId ID of the given edge.
     * @return (double) the length of the given edge.
     */
    public double edgeLength(int edgeId) {
        return edges.length(edgeId);
    }

    /**
     * returns the elevation gain of a given edge.
     *
     * @param edgeId ID of the given edge to extract the elevation gain from.
     * @return (double) the elevation gain of a give edge.
     */
    public double edgeElevationGain(int edgeId) {
        return edges.elevationGain(edgeId);
    }

    /**
     * return the Function (linear by parts) sampling the profiles of a given edge.
     *
     * @param edgeId ID of the edge to extract the profile samples from.
     * @return the Function sampling the profiles of a given edge.
     */
    public DoubleUnaryOperator edgeProfile(int edgeId) {
        return (edges.hasProfile(edgeId) ? Functions.sampled(edges.profileSamples(edgeId), edgeLength(edgeId))
                : Functions.constant(Double.NaN));
    }
}
