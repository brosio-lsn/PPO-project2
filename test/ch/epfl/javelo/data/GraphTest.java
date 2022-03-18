package ch.epfl.javelo.data;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.projection.Ch1903;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;
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
    static Graph b;
    static Path filePath = Path.of("lausanne/nodes_osmid.bin");
    LongBuffer osmIdBuffer;


        FileChannel channel;

    {
        try {
            channel = FileChannel.open(filePath);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    static {
        try {
            b = Graph.loadFrom(Path.of("lausanne"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Test
    void loadFromThrowsIOExceptionForIllegalPath() {
        assertThrows(IOException.class, () -> Graph.loadFrom(Path.of("kfjaejbzf")));
    }
    @Test
    void loadFrom() throws IOException{
            osmIdBuffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size())
                    .asLongBuffer();
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
        System.out.println(b.nodeOutEdgeId(2023, 0));
        System.out.println(b.edgeAttributes(4095));
        System.out.println(b.edgeLength(1));
        System.out.println(b.edgeProfile(1));
        System.out.println(b.edgeElevationGain(1));
        System.out.println(b.edgeTargetNodeId(1));

    }

    @Test
    void nodeCount() throws IOException {
        osmIdBuffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size())
                .asLongBuffer();
        System.out.println(osmIdBuffer.capacity());
        assertEquals(osmIdBuffer.capacity(), b.nodeCount());
    }

    @Test
    void nodePoint() {
        assertTrue(b.nodePoint(1).distanceTo(new PointCh(2549212.9375,1166183.5625 )) < 1);
    }

    @Test
    void nodeOutDegree() {
        assertEquals(b.nodeOutDegree(1), 2);
    }

    @Test
    void nodeOutEdgeId() {
        int actual = b.nodeOutEdgeId(2, 0);
        int expected = 3;
        assertEquals(expected, actual);
        System.out.println(b.nodeOutEdgeId(1, 0));

    }

    @Test
    void nodeClosestTo() {
      //  assertEquals(b.nodeClosestTo(new PointCh(2549212.9375, 1166183.5625), 0.1), 1);
        assertEquals(b.nodeClosestTo(b.nodePoint(0), 0), 0);
        assertEquals(b.nodeClosestTo(b.nodePoint(0), 1000000), 0);
            int actual = b.nodeClosestTo(new PointCh(SwissBounds.MAX_E - 100000, SwissBounds.MAX_N - 100000), 0);
            int expected = -1;
            assertEquals(expected, actual);
        }

    @Test
    void edgeTargetNodeIdWorksOnNormal() {
        assertEquals(1,b.edgeTargetNodeId(0));
    }

    @Test
    void edgeIsInverted() {
        assertFalse(b.edgeIsInverted(0));
    }

    @Test
    void edgeAttributes() {
        int actual = b.nodeOutEdgeId(10, b.nodeOutDegree(10) - 1);
        //assertEquals(myGraph.nodeOutDegree(1), 2);
        int expected = b.nodeOutDegree(1);
        System.out.println(actual);
        System.out.println(expected);
    }

    @Test
    void edgeLength() {
       assertEquals(b.edgeLength(0), new PointCh(2549278.75, 1166252.3125).distanceTo(
               new PointCh(2549212.9375, 1166183.5625)), 1e-1);
    }

    @Test
    void elevationGain() {
    }

    @Test
    void edgeProfile() {
        System.out.println(b.nodePoint(0));
        System.out.println(b.nodePoint(1));
        DoubleUnaryOperator aa = b.edgeProfile(0);
        double coucou = aa.applyAsDouble(0);
        double yolo = aa.applyAsDouble(b.edgeLength(0));
        System.out.println(coucou);
        System.out.println(yolo);
    }
    public static final double DELTA = 0.7;

    @Test
    void loadFilesandCount() throws IOException {
        Path filePath = Path.of("lausanne");
        Graph e = Graph.loadFrom(filePath);
        int a = e.nodeCount();
        assertEquals(212679, a);
    }

    @Test
    void throwsExcpetionwrongPath() {
        Path filePath = Path.of("haha");
        assertThrows(IOException.class, () -> {
            Graph.loadFrom(filePath);
        });
    }

    @Test
    void correctPointCh() throws IOException {

        Path filePath = Path.of("lausanne");
        Graph e = Graph.loadFrom(filePath);
        PointCh un = e.nodePoint(0);
        PointCh deux = e.nodePoint(212678);
        PointCh expectedUn  = new PointCh(2549278.75,1166252.3125);
        PointCh expectedDeux = new PointCh(2526526.1875,1166115.625);
        //Emplacement : 46,5108142, 6,4278990

        double coordinateE = Ch1903.e(Math.toRadians(6.4278990), Math.toRadians(46.5108142));
        double coordinateN = Ch1903.n(Math.toRadians(6.4278990),Math.toRadians(46.5108142));
        PointCh Expected3 = new PointCh(coordinateE,coordinateN);
        double lon = Expected3.lon();
        double lat = Expected3.lat();
        System.out.println(" lon " + Math.toDegrees(lon));
        System.out.println("lat " + Math.toDegrees(lat));


        assertEquals(expectedUn, un);
        assertEquals(expectedDeux, deux);
        assertEquals(Expected3.e(),e.nodePoint(191227).e(), DELTA);
        assertEquals(Expected3.n(),e.nodePoint(191227).n(), DELTA);

    }

    @Test
    void nodeClosestToTest() throws IOException {
        Path filePath = Path.of("lausanne");
        Graph e = Graph.loadFrom(filePath);
        int a = e.nodeClosestTo(new PointCh(2549279,1166253), 100);
        int b = e.nodeClosestTo(new PointCh(2526525,1166114),100);
        int c = e.nodeClosestTo(new PointCh(2530000,1150000),1000000);
        System.out.println(c);
        int noNode = e.nodeClosestTo(new PointCh(2526525,1166114),0);//970,2552
        int aExpected = 0;
        int bExpected = 212678;

        assertEquals(aExpected, a);
        assertEquals(bExpected, b);
        assertEquals(-1, noNode);

        //46.769586, 6.777362
        double coordinateE = Ch1903.e(Math.toRadians(6.777362), Math.toRadians(46.769586));
        double coordinateN = Ch1903.n(Math.toRadians(6.777362),Math.toRadians(46.769586));
        int a1 = e.nodeClosestTo(new PointCh(coordinateE, coordinateN), 1000);
        assertEquals(-1,a1);

        //46.434182, 6.291968
        double coordinateE1 = Ch1903.e(Math.toRadians(6.291968), Math.toRadians(46.434182));
        double coordinateN1 = Ch1903.n(Math.toRadians(6.291968),Math.toRadians(46.434182));
        int a2 = e.nodeClosestTo(new PointCh(coordinateE1, coordinateN1), 1000);
        assertEquals(-1,a2);

    }

    @Test
    void nodeClosestToSecond() throws IOException{
        Path filePath = Path.of("lausanne");
        Graph e = Graph.loadFrom(filePath);

        double coordinateE = Ch1903.e(Math.toRadians(6.5722746), Math.toRadians(46.5283247));
        double coordinateN = Ch1903.n(Math.toRadians(6.5722746),Math.toRadians(46.5283247));
        int a = e.nodeClosestTo(new PointCh(coordinateE, coordinateN), 100);
        int expected  = 153137;
        assertEquals(expected,a);




        //noeud osm 459836822
        double coordinateE1 = Ch1903.e(Math.toRadians(6.446884), Math.toRadians(46.620389));//point au hasard google maps
        double coordinateN1 = Ch1903.n(Math.toRadians(6.446884),Math.toRadians(46.620389));
        int a1 = e.nodeClosestTo(new PointCh(coordinateE1, coordinateN1), 1000);
        int expected1  = 209775;
        assertEquals(expected1,a1);



        double coordinateE2 = Ch1903.e(Math.toRadians(6.449376), Math.toRadians(46.622229));//point au hasard google maps
        double coordinateN2 = Ch1903.n(Math.toRadians(6.449376),Math.toRadians(46.622229));
        int a2 = e.nodeClosestTo(new PointCh(coordinateE2, coordinateN2), 100);
        int expected2  = 210000;
        assertEquals(expected2,a2);

        double coordinateE3 = Ch1903.e(Math.toRadians(6.449376), Math.toRadians(46.622229));//point au hasard google maps
        double coordinateN3 = Ch1903.n(Math.toRadians(6.449376),Math.toRadians(46.622229));
        int a3 = e.nodeClosestTo(new PointCh(coordinateE3, coordinateN3), 10);
        int expected3  = -1;
        assertEquals(expected3,a3);

    }

    @Test
    void nodeOutDegreeTest() throws IOException{
        Path filePath = Path.of("lausanne");
        Graph e = Graph.loadFrom(filePath);
        //System.out.println(e.nodeOutDegree(150000));
        assertEquals(2,e.nodeOutDegree(150000));
        assertEquals(2,e.nodeOutDegree(180000));
        assertEquals(2,e.nodeOutDegree(190000));
        assertEquals(3,e.nodeOutDegree(191227));
        assertEquals(3,e.nodeOutDegree(210000));
    }

    @Test
    void nodeOutEdgeIndexTest() throws IOException{
        Path filePath = Path.of("lausanne");
        Graph e = Graph.loadFrom(filePath);
        System.out.println(e.nodeOutEdgeId(191227,0));//411550
        System.out.println(e.nodeOutEdgeId(190000,0));//408989
        System.out.println(e.nodeOutEdgeId(210000,0));//451659
        System.out.println(e.nodeOutEdgeId(210000,1));//451660
        System.out.println(e.nodeOutEdgeId(210000,2));//451661
        //just see if coherent
        assertEquals(3,3);
    }


    @Test
    void edgeAttributeTest() throws IOException {
        Path filePath = Path.of("lausanne");
        Graph e = Graph.loadFrom(filePath);


        int nodeOut = e.nodeOutDegree(2022);
        int EdgeOutIndex = e.nodeOutEdgeId(2022,0);
        System.out.println("node out " + nodeOut);
        System.out.println("nodeOutIndex " + EdgeOutIndex);
        AttributeSet a = e.edgeAttributes(2);
        AttributeSet b = e.edgeAttributes(411550);//cohérent highway-service
        AttributeSet c = e.edgeAttributes(408989);//cohérent highway-service
        AttributeSet d1 = e.edgeAttributes(451659); //Chemin OSM 38775500 : {highway=track,tracktype=grade1,surface=paved}
        AttributeSet d2 = e.edgeAttributes(451660); // Chemin: Route de La Chaux (398806539)
        // {highway=unclassified,tracktype=grade1,surface=paved}
        AttributeSet d3 = e.edgeAttributes(451661); // Chemin: Route de La Chaux (398806539)
        // {highway=unclassified,tracktype=grade1,surface=paved}
        System.out.println(b);
        System.out.println(c);
        System.out.println(d1);
        System.out.println(d2);
        System.out.println(d3);
        assertEquals(-1, -1);
    }

    @Test
    void edgeInverted() throws IOException{
        Path filePath = Path.of("lausanne");
        Graph e = Graph.loadFrom(filePath);
        //un sur deux doit être inversé
        System.out.println(e.edgeIsInverted(451660));
        System.out.println(e.edgeIsInverted(451661));

    }

    @Test
    void targetNode() throws IOException{
        Path filePath = Path.of("lausanne");
        Graph e = Graph.loadFrom(filePath);
        System.out.println(e.edgeTargetNodeId(451660));
        assertEquals(209773,e.edgeTargetNodeId(451660));
    }

    @Test
    void edgeProfileTest()throws IOException{
        Path filePath = Path.of("lausanne");
        Graph e = Graph.loadFrom(filePath);
        //arrêtes qui n'ont pas de profil : 3558 3573 3810 3812 3879 3880 3995 4011 4036 4039
        DoubleUnaryOperator test = e.edgeProfile(0);
        double b = test.applyAsDouble(2);
        System.out.println(b);
        e.edgeProfile(0);
        //length of the array : 49
        //length of the edge : 95.125
        float[] samples = new float[]{606.75f,606.8125f,607.0f,607.125f,607.1875f,607.25f,607.375f,607.4375f,607.5625f,607.625f,607.6875f,
                607.8125f,607.8125f,607.875f,607.9375f,608.0f,608.0f,608.125f,608.1875f,608.125f, 608.1875f,608.25f,608.25f,608.25f,608.375f,
                608.4375f,608.4375f,608.5f,608.5625f,608.625f,608.6875f,608.75f,608.8125f,608.8125f,608.875f,608.9375f,608.9375f,609.0f,609.0625f,
                609.0625f,609.125f,609.125f,609.1875f,609.1875f,609.25f,609.3125f,609.375f,609.375f,609.4375f};

        //607.09691195795 : good position
        System.out.println("hey" + e.edgeProfile(0).applyAsDouble(5.5));
        System.out.println(e.edgeProfile(451660).applyAsDouble(100));

        assertEquals(Functions.constant(Double.NaN),e.edgeProfile(3558));
        assertEquals(Functions.constant(Double.NaN),e.edgeProfile(4039));
        assertNotEquals(Functions.constant(Double.NaN), e.edgeProfile(4040));
        assertEquals(e.edgeProfile(0).applyAsDouble(5.5), Functions.sampled(samples, 95.125).applyAsDouble(5.5));


    }

    @Test
    void elevationGainTest()throws IOException{
        Path filePath = Path.of("lausanne");
        Graph e = Graph.loadFrom(filePath);
        System.out.println(e.edgeElevationGain(451660));
        double coordinateE3 = Ch1903.e(Math.toRadians(6.628059), Math.toRadians(46.509635));//point au hasard google maps
        double coordinateN3 = Ch1903.n(Math.toRadians(6.628059),Math.toRadians(46.509635));
        int a3 = e.nodeClosestTo(new PointCh(coordinateE3, coordinateN3), 10);
        System.out.println(a3);//134119
        System.out.println(e.nodeOutDegree(134119));
        System.out.println(e.nodeOutEdgeId(134119,0));
        System.out.println(e.nodeOutEdgeId(134119,1));
        System.out.println(e.nodeOutEdgeId(134119,2));
        //287113
        //287114 //Av. Ouchy
        //287115 Av. Ouchy
        System.out.println(e.edgeAttributes(287113));
        System.out.println(e.edgeAttributes(287114));
        System.out.println(e.edgeAttributes(287115));
        System.out.println(e.edgeElevationGain(287114));//0.8125
        System.out.println(e.edgeElevationGain(287113));
        assertEquals(0.5, e.edgeElevationGain(287113),DELTA);

    }



    @Test
    void testOSMData() throws IOException{
        Path filePath = Path.of("lausanne/nodes_osmid.bin");
        LongBuffer osmIdBuffer;
        try (FileChannel channel = FileChannel.open(filePath)) {
            osmIdBuffer = channel
                    .map(FileChannel.MapMode.READ_ONLY, 0, channel.size())
                    .asLongBuffer();
        }
        int minInd  = 0;
        long min = Long.MAX_VALUE;
        int maxInd = 0;
        long max = 0;
        for(int i = 0; i < osmIdBuffer.capacity(); i++){
            if(osmIdBuffer.get(i) < min){
                minInd = i;
                min = osmIdBuffer.get(i);
            }
            if(osmIdBuffer.get(i) > max){
                maxInd = i;
                max = osmIdBuffer.get(i);
            }
        }

        int index = 0;
        /*
        for(int i = 0; i < osmIdBuffer.capacity(); i++) {
            if (osmIdBuffer.get(i) == 459836822) {
                index = i;
            }


        }

         */

        for(int i = 0; i < osmIdBuffer.capacity(); i++) {
            if (osmIdBuffer.get(i) == 364785322 ) {
                index = i;
            }


        }
        System.out.println("index" + index);
        System.out.println(osmIdBuffer.get(210000));
        System.out.println(osmIdBuffer.get(209775));
        System.out.println(osmIdBuffer.get(150000));
        System.out.println(osmIdBuffer.get(180000));
        System.out.println(osmIdBuffer.get(190000));
        System.out.println(osmIdBuffer.get(191227));
        System.out.println(osmIdBuffer.get(109350));
        System.out.println(osmIdBuffer.get(134119));
        System.out.println(osmIdBuffer.get(134118));
        System.out.println(osmIdBuffer.get(134120));
        System.out.println(osmIdBuffer.get(134121));
        System.out.println(osmIdBuffer.get(209768));
        System.out.println(osmIdBuffer.get(209949));
        System.out.println(osmIdBuffer.get(209773));
        System.out.println(osmIdBuffer.get(194890));
        /*
        System.out.println(index);
        System.out.println(minInd); //199954
        System.out.println(maxInd); //153137
        System.out.println(min); // 172237
        System.out.println(max); // 9394865704

         */

        assertEquals(310876657,osmIdBuffer.get(2022));
    }

}