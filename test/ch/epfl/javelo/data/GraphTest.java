package ch.epfl.javelo.data;

import ch.epfl.javelo.projection.Ch1903;
import ch.epfl.javelo.projection.PointCh;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.text.FieldPosition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.DoubleUnaryOperator;

import static org.junit.jupiter.api.Assertions.*;

class GraphTest {

    @Test
    void loadFromThrowsIOExceptionForIllegalPath() {
        assertThrows(IOException.class, () -> Graph.loadFrom(Path.of("kfjaejbzf")));
    }
    @Test
    void loadFrom() throws IOException{
        Graph b = Graph.loadFrom(Path.of("lausanne"));
        Path filePath = Path.of("lausanne/nodes_osmid.bin");
        LongBuffer osmIdBuffer;
        try (FileChannel channel = FileChannel.open(filePath)) {
            osmIdBuffer = channel
                    .map(FileChannel.MapMode.READ_ONLY, 0, channel.size())
                    .asLongBuffer();
        }

        System.out.println(osmIdBuffer.get(1));
        System.out.println(osmIdBuffer.get(0));
        System.out.println(b.nodePoint(1));
        System.out.println(b.nodePoint(0));
        PointCh a = b.nodePoint(0);
        PointCh c = b.nodePoint(1);
        System.out.println("distance : " + c.distanceTo(a));
        System.out.println(b.nodeOutDegree(1));
        assertEquals(b.nodeCount(), osmIdBuffer.capacity());
        assertEquals(b.nodeClosestTo(new PointCh(2549212.9375, 1166183.5625), 0.1), 1);
        System.out.println(b.nodeOutEdgeId(2022,0));
        System.out.println(b.edgeAttributes(4095));
        System.out.println(b.edgeLength(1));
        System.out.println(b.edgeProfile(1));
        System.out.println(b.edgeElevationGain(1));
        System.out.println(b.edgeTargetNodeId(1));

    }

    @Test
    void nodeCount() {
    }

    @Test
    void nodePoint() {
    }

    @Test
    void nodeOutDegree() {
    }

    @Test
    void nodeOutEdgeId() {
    }

    @Test
    void nodeClosestTo() {
    }

    @Test
    void edgeTargetNodeId() {
    }

    @Test
    void edgeIsInverted() {
    }

    @Test
    void edgeAttributes() {
    }

    @Test
    void edgeLength() {
    }

    @Test
    void elevationGain() {
    }

    @Test
    void edgeProfile() {
    }
    private Graph getGraph() throws IOException {
        return Graph.loadFrom(Path.of("lausanne"));
    }

    //Pas un test
    @Test
    void loadFrom2() throws IOException {

    }

    //Pas un test
    @Test
    void nodeCountWorksOnKnowValue() throws IOException {
        Graph graph = getGraph();
        System.out.println(graph.nodeCount());
    }

    //Test
    @Test
    void nodePointWorksOnKnowValue() throws IOException {
        Graph graph = getGraph();
        //LongBuffer nodeOSMId = readNodeOSMId();
        //System.out.println(nodeOSMId.get(0)); // 1684019323

        PointCh actual = graph.nodePoint(0);
        double lat = Math.toRadians(46.6455770);
        double lon = Math.toRadians(6.7761194);

        assertEquals(lat, actual.lat(), 10e-7);
        assertEquals(lon, actual.lon(), 10e-7);
    }

    //Test
    @Test
    void nodeOutDegree2() throws IOException {
        Graph graph = getGraph();

        int actual1 = graph.nodeOutDegree(0); //1684019323
        assertEquals(1, actual1);
        int actual2 = graph.nodeOutDegree(1); //1684019310
        assertEquals(2, actual2);
        int actual3 = graph.nodeOutDegree(100_000); //2101684853
        assertEquals(3, actual3);
    }

    //Test
    @Test
    void nodeOutEdgeId2() throws IOException {
        Graph graph = getGraph();
        int actual1 = graph.nodeOutEdgeId(0, 0); //1684019323
        assertEquals(0, actual1);
    }

    //Test
    @Test
    void nodeClosestTo2() throws IOException {
        Graph graph = getGraph();

        double e = Ch1903.e(Math.toRadians(6.77653), Math.toRadians(46.64608)); //osmid: 1684019323
        double n = Ch1903.n(Math.toRadians(6.77653), Math.toRadians(46.64608));
        PointCh point = new PointCh(e, n);
        int actual1 = graph.nodeClosestTo(point, 100);
        int actual2 = graph.nodeClosestTo(point, 0);
        System.out.println(graph.edgeAttributes(36234));

        assertEquals(0, actual1);
        assertEquals(-1, actual2);
    }

    //Test
    @Test
    void edgeTargetNodeId2() throws IOException {
        Graph graph = getGraph();

        int actual1 = graph.edgeTargetNodeId(0);
        assertEquals(1, actual1);
    }

    //Test
    @Test
    void edgeIsInverted2() throws IOException {
        Graph graph = getGraph();

        assertFalse(graph.edgeIsInverted(0));
        assertTrue(graph.edgeIsInverted(334630));
        //assertFalse(graph.edgeIsInverted(36234));
    }

    //Test
    @Test
    void edgeAttributes2() throws IOException {
        Graph graph = getGraph();

        AttributeSet actual1 = graph.edgeAttributes(0);
        AttributeSet expected1 = AttributeSet.of(Attribute.HIGHWAY_TRACK, Attribute.TRACKTYPE_GRADE1);
        assertEquals(expected1.bits(), actual1.bits());

        AttributeSet expected2 = AttributeSet.of(Attribute.BICYCLE_USE_SIDEPATH, Attribute.HIGHWAY_TERTIARY, Attribute.SURFACE_ASPHALT);
        AttributeSet actual2 = graph.edgeAttributes(362164);
        assertEquals(expected2.bits(), actual2.bits());
    }

    //Test
    @Test
    void edgeLength2() throws IOException {
        Graph graph = getGraph();
        double actual = graph.edgeLength(335275);
        assertEquals(24, actual, 10e-1); //  /!\ expected -> lack of precision
    }

    //TEst
    @Test
    void edgeElevationGain() throws IOException {
        Graph graph = getGraph();
        double actual1 = graph.edgeElevationGain(335275);
        assertEquals(1, actual1, 10e-2); //  /!\ expected -> lack of precision

        double actual2 = graph.edgeElevationGain(293069); // edge entre 289087937 (osm) et 570300687 (osm)
        assertEquals(8, actual2, 10e-1); //  /!\ expected -> lack of precision
    }

    //Test
    @Test
    void edgeProfile2() throws IOException {
        Graph graph = getGraph();
        DoubleUnaryOperator func = graph.edgeProfile(335275);
        assertEquals(390,func.applyAsDouble(0),1);
        DoubleUnaryOperator func2 = graph.edgeProfile(293912);
        assertEquals(490,func2.applyAsDouble(0),4);
        assertEquals(496,func2.applyAsDouble(34),1);

    }
}