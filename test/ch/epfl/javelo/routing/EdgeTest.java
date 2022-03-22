package ch.epfl.javelo.routing;
import ch.epfl.javelo.Functions;
import ch.epfl.javelo.Math2;
import ch.epfl.javelo.data.*;
import ch.epfl.javelo.projection.Ch1903;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.function.DoubleUnaryOperator;

import static org.junit.jupiter.api.Assertions.*;

public class EdgeTest {
    static double DELTA = 0.7;
    @Test
    void closestToTest() throws IOException {
        Path filePath = Path.of("lausanne");
        Graph e = Graph.loadFrom(filePath);
        Edge ed = Edge.of(e, 0,0,1);
        PointCh un = e.nodePoint(0);
        PointCh deux = e.nodePoint(1);
        System.out.println(un);
        System.out.println(deux);
        System.out.println(Math2.projectionLength(un.e(),un.n(),deux.e(),deux.n(),2549250, 1166200));
        Edge ed1 = Edge.of(e, 287113,133636,134119);//Chemin de Roseneck
        // 46,5094768, 6,6293859
        double coordinateE3 = Ch1903.e(Math.toRadians(6.6293859), Math.toRadians(46.5094768));//point au hasard google maps
        double coordinateN3 = Ch1903.n(Math.toRadians(6.6293859),Math.toRadians(46.5094768));
        PointCh point = new PointCh(coordinateE3, coordinateN3);
        System.out.println(ed1.length());
        System.out.println(ed1.positionClosestTo(point));// cohérent sur la map

        //point déjà noeud
        //46,5097131, 6,6280890
        //46,5093368, 6,6299992 dernier noeud
        double coordinateE4 = Ch1903.e(Math.toRadians(6.6299992 ), Math.toRadians(46.5093368));//point au hasard google maps
        double coordinateN4 = Ch1903.n(Math.toRadians(6.6299992 ),Math.toRadians(46.5093368));
        PointCh point2 = new PointCh(coordinateE4, coordinateN4);
        System.out.println(ed1.length());
        System.out.println(ed1.positionClosestTo(point2));

        //46.509504, 6.630912
        double coordinateE5 = Ch1903.e(Math.toRadians(6.630912), Math.toRadians(46.509504));
        double coordinateN5 = Ch1903.n(Math.toRadians(6.630912), Math.toRadians(46.509504));
        PointCh point5 = new PointCh(coordinateE5, coordinateN5);
        assertTrue(ed1.positionClosestTo(point5) < 0);

        //46.510120, 6.627729
        double coordinateE6 = Ch1903.e(Math.toRadians(6.627729), Math.toRadians(46.510120));
        double coordinateN6 = Ch1903.n(Math.toRadians(6.627729), Math.toRadians(46.510120));
        PointCh point6 = new PointCh(coordinateE6, coordinateN6);
        assertTrue(ed1.positionClosestTo(point6) > ed1.length());



        //Edge ed = Edge.of(e,287114,)
        assertEquals(0, ed1.positionClosestTo(point2),DELTA);
        assertEquals(57.669817987113, ed.positionClosestTo(new PointCh(2549250,1166200)));

        Edge ed2 = Edge.of(e,451660,209773,210000);

        //46.622584, 6.448870
        //46.623154, 6.449203
        //46.622644, 6.450091
        double coordinateE = Ch1903.e(Math.toRadians(6.450091), Math.toRadians(46.622644));//point au hasard google maps
        double coordinateN = Ch1903.n(Math.toRadians(6.450091),Math.toRadians(46.622644));
        PointCh pointTest = new PointCh(coordinateE,coordinateN);
        PointCh point3 = ed2.pointAt(0);
        PointCh point4 = ed2.pointAt(162);
        //assertEquals(0,ed2.positionClosestTo(pointTest),DELTA);
        assertEquals(0,ed2.positionClosestTo(point3),DELTA);
        assertEquals(162,ed2.positionClosestTo(point4),DELTA);
    }


    @Test
    void pointAtTest() throws IOException {
        Path filePath = Path.of("lausanne");
        Graph e = Graph.loadFrom(filePath);
        Edge ed = Edge.of(e, 0,0,1);
        Edge ed1 = Edge.of(e, 287113,133636,134119);

        System.out.println(ed1.length());//74.875
        System.out.println(ed1.pointAt(74.875));
        PointCh point = ed1.pointAt(74.875);
        System.out.println(Math.toDegrees(point.lat()));
        System.out.println(Math.toDegrees(point.lon()));

        PointCh point2 = ed1.pointAt(0);
        System.out.println(Math.toDegrees(point2.lat()));
        System.out.println(Math.toDegrees(point2.lon()));
        //ed.pointAt(2549248.2);

        PointCh point3 = ed1.pointAt(50); // cohérent sur lsa map
        System.out.println(Math.toDegrees(point3.lat()));
        System.out.println(Math.toDegrees(point3.lon()));

        assertEquals(e.nodePoint(134119).e(),point.e(),DELTA);
        assertEquals(e.nodePoint(134119).n(),point.n(),DELTA);
        assertEquals(e.nodePoint(133636).e(),point2.e(),DELTA);
        assertEquals(e.nodePoint(133636).n(),point2.n(),DELTA);
        assertEquals(new PointCh(2549237.2388304863,1166208.9485052563),ed.pointAt(60));

    }


    @Test
    void elevationAtTest() throws IOException{
        Path filePath = Path.of("lausanne");
        Graph e = Graph.loadFrom(filePath);
        Edge ed = Edge.of(e, 0,0,1);
        Edge ed1 = Edge.of(e, 287113,134119,133636);
        System.out.println(ed1.elevationAt(0));// altitude ~397m
        System.out.println(ed1.elevationAt(74.875)); //altitude swisstopo ~397m
        assertEquals(397,ed1.elevationAt(0),DELTA);
        assertEquals(397,ed1.elevationAt(ed1.length()),DELTA);

        //Find an another Example
        System.out.println(e.nodeOutDegree(210000));
        System.out.println(e.nodeOutEdgeId(210000,0));
        System.out.println(e.nodeOutEdgeId(210000,1));
        System.out.println(e.nodeOutEdgeId(210000,2));
        //451660 route de la chaux
        System.out.println(e.edgeAttributes(451659));
        System.out.println(e.edgeAttributes(451660));
        System.out.println(e.edgeAttributes(451661));
        //Edge ed2 = Edge.of(e,451660,209768 ,209949);
        Edge ed2 = Edge.of(e,451660,209773,210000);
        System.out.println(ed2.length());
        System.out.println(ed2.elevationAt(0));// altitude ~627
        System.out.println(ed2.elevationAt(160));//altitude 600m

        PointCh point3 = ed2.pointAt(0); // noeud 209773
        System.out.println(Math.toDegrees(point3.lat()));
        System.out.println(Math.toDegrees(point3.lon()));

        PointCh point4 = ed2.pointAt(162); // noeud 210000
        System.out.println(Math.toDegrees(point4.lat()));
        System.out.println(Math.toDegrees(point4.lon()));

        assertEquals(627,ed2.elevationAt(0),DELTA);
        assertEquals(623,ed2.elevationAt(162),DELTA);



    }
        @Test
    void positionClosestTo() {
        float[] yeet = {30f, 31f, 30.5f, 34f, 29f, 30f, 35f, 29.5f, 33f, 31f,60f};
        PointCh fromPoint= new PointCh(SwissBounds.MIN_E+500, SwissBounds.MIN_N+200);
        PointCh toPoint= new PointCh(SwissBounds.MIN_E+510, SwissBounds.MIN_N+205);
        double length = Math2.norm((toPoint.n()-fromPoint.n()),(toPoint.e()-fromPoint.e()));
        DoubleUnaryOperator yo = Functions.sampled(yeet,length);
        Edge edge = new Edge(0,50,fromPoint,toPoint, length, yo);
        double a = (toPoint.n()-fromPoint.n())/(toPoint.e()-fromPoint.e());
        double expected=length/2.0;
        PointCh pointOndroite = edge.pointAt(expected);
        PointCh pointNotOnDroite = new PointCh(SwissBounds.MIN_E+53, -1/a*(SwissBounds.MIN_E+53)+pointOndroite.n()+1/a*pointOndroite.e());
        assertEquals(expected,edge.positionClosestTo(pointNotOnDroite), 10E-2);

        expected=length;
        pointOndroite = edge.pointAt(expected);
        pointNotOnDroite = new PointCh(SwissBounds.MIN_E+53, -1/a*(SwissBounds.MIN_E+53)+pointOndroite.n()+1/a*pointOndroite.e());
        assertEquals(expected,edge.positionClosestTo(pointNotOnDroite), 10E-3);

        expected=0;
        pointOndroite = edge.pointAt(expected);
        pointNotOnDroite = new PointCh(SwissBounds.MIN_E+53, -1/a*(SwissBounds.MIN_E+53)+pointOndroite.n()+1/a*pointOndroite.e());
        assertEquals(expected,edge.positionClosestTo(pointNotOnDroite), 10E-3);

        expected=-length*0.75;
        pointOndroite = edge.pointAt(expected);
        pointNotOnDroite = new PointCh(SwissBounds.MIN_E+51, -1/a*(SwissBounds.MIN_E+51)+pointOndroite.n()+1/a*pointOndroite.e());
        assertEquals(expected,edge.positionClosestTo(pointNotOnDroite), 10E-3);

        expected=2*length;
        pointOndroite = edge.pointAt(expected);
        pointNotOnDroite = new PointCh(SwissBounds.MIN_E+51, -1/a*(SwissBounds.MIN_E+51)+pointOndroite.n()+1/a*pointOndroite.e());
        assertEquals(expected,edge.positionClosestTo(pointNotOnDroite), 10E-3);
    }

    @Test
    void AltitudeAtGivenPosition() {
        float[] yeet = {30f, 31f, 30.5f, 34f, 29f, 30f, 35f, 29.5f, 33f, 31f,60f};
        PointCh fromPoint= new PointCh(SwissBounds.MIN_E+500, SwissBounds.MIN_N+200);
        PointCh toPoint= new PointCh(SwissBounds.MIN_E+510, SwissBounds.MIN_N+200);
        double length = Math2.norm((toPoint.n()-fromPoint.n()),(toPoint.e()-fromPoint.e()));
        DoubleUnaryOperator yo = Functions.sampled(yeet,length);
        Edge edge = new Edge(0,50,fromPoint,toPoint, length, yo);
        assertEquals(45.5, edge.elevationAt(9.5));
        assertEquals(60, edge.elevationAt(92));
        assertEquals(30, edge.elevationAt(-10));
        assertEquals(30, edge.elevationAt(0));
        assertEquals(60f, edge.elevationAt(length));

        float[] yeet2 = {28f, 30f, 30.5f, 34f, 26f, 30f, 37f, 29.5f, 32f, 31f,80f};
        PointCh fromPoint2= new PointCh(SwissBounds.MIN_E+510, SwissBounds.MIN_N+205);
        PointCh toPoint2= new PointCh(SwissBounds.MIN_E+522, SwissBounds.MIN_N+211);
        double length2 = Math2.norm((toPoint2.n()-fromPoint2.n()),(toPoint2.e()-fromPoint2.e()));
        DoubleUnaryOperator yo2 = Functions.sampled(yeet2,length);
        Edge edge2 = new Edge(50,62,fromPoint2,toPoint2, length2, yo2);

        assertEquals(80f, edge2.elevationAt(length));
    }


    Graph actual1 = Graph.loadFrom(Path.of("lausanne"));

    public EdgeTest() throws IOException {
    }

    @Test
    void ofWorks() throws IOException {
        Graph actual1 = Graph.loadFrom(Path.of("lausanne"));
        Edge hay = Edge.of(actual1, 1000, 2345, 5436);
    }

    @Test
    void positionClosestToWorks() throws IOException {
        Graph actual1 = Graph.loadFrom(Path.of("lausanne"));
        Edge actualEdge = Edge.of(actual1, 1000, 2345, 5436);
        assertEquals(-53538.84482952522, actualEdge.positionClosestTo(new PointCh(2601098, 1101654)));
    }

/*
    @Test
    void pointAtWorks() throws IOException {
        Graph actual1 = Graph.loadFrom(Path.of("lausanne"));
        Edge actualEdge = Edge.of(actual1, 1000, 2345, 5436);
        assertEquals(new PointCh(2539399.27250234, 1164288.767963147), actualEdge.pointAt(100));
    }
*/

    @Test
    void elevationAtWorks() {
        Edge actualEdge = Edge.of(actual1, 1000, 2345, 5436);
        assertEquals(841.125, actualEdge.elevationAt(100));
    }

    @Test
    void HorizontalEdgeWorksCorrectlyForLimits() {
        PointCh fromPoint = new PointCh(2485010, 1076000);
        PointCh toPoint = new PointCh(2485020, 1076000);
        float[] type3Array = new float[]{384.75f, 384.6875f, 384.5625f, 384.5f, 384.4375f, 384.375f, 384.3125f, 384.25f, 384.125f, 384.0625f,};
        DoubleUnaryOperator profile = Functions.sampled(type3Array, 10);
        DoubleUnaryOperator squared = new DoubleUnaryOperator() {
            @Override
            public double applyAsDouble(double operand) {
                return Math.pow(operand, 2);
            }
        };
        Edge edge1 = new Edge(0, 3, fromPoint, toPoint, 10, squared);
        PointCh pointToTest = new PointCh(2600000, 1085000);
        assertEquals(0, edge1.positionClosestTo(fromPoint));
        assertEquals(10, edge1.positionClosestTo(toPoint));
        //Rajouter pr un point quelconque.
        assertEquals(fromPoint, edge1.pointAt(0));
        assertEquals(toPoint, edge1.pointAt(10));
        assertEquals(new PointCh(2485015, 1076000), edge1.pointAt(5));
        assertEquals(9, edge1.elevationAt(3));
        Edge edge2 = new Edge(0, 3, fromPoint, toPoint, 10, profile);
        assertEquals(384.75f, edge2.elevationAt(0));
    }

/*    @Test
    void EdgeIsFine() {
        IntBuffer forNodes = IntBuffer.wrap(new int[]{
                2_842_000 << 4,
                1_197_000 << 4,
                0x2_800_0015,
                2_657_112 << 4,
                1_080_000 << 4,
                0x2_674_0215
        });
        GraphNodes graphNodes1 = new GraphNodes(forNodes);

        ByteBuffer edgesBuffer = ByteBuffer.allocate(10);
        // Sens : inversé. Nœud destination : 12.
        edgesBuffer.putInt(0, 12);
        //Longueur : 0x10.b m (= 16.6875 m)
        edgesBuffer.putShort(4, (short) 0x10_b);
        // Dénivelé : 0x10.0 m (= 16.0 m)
        edgesBuffer.putShort(6, (short) 0x10_0);
        // Identité de l'ensemble d'attributs OSM : 1
        edgesBuffer.putShort(8, (short) 2022);
        IntBuffer profileIds = IntBuffer.wrap(new int[]{
                // Type : 0. Index du premier échantillon : 1.
                (3 << 30) | 1
        });

        ShortBuffer elevations = ShortBuffer.wrap(new short[]{
                (short) 0,
                (short) 0x180C, (short) 0xFEFF,
                (short) 0xFFFE, (short) 0xF000 //Type3
        });
        GraphEdges edges1 =
                new GraphEdges(edgesBuffer, profileIds, elevations);


        float[] type3Array = new float[]{
                384.75f, 384.6875f, 384.5625f, 384.5f, 384.4375f,
                384.375f, 384.3125f, 384.25f, 384.125f, 384.0625f,
        };

        ByteBuffer buffer = ByteBuffer.wrap(new byte[]{
                0, 0, 0, 12, 0, 10});
        GraphSectors sectors1 = new GraphSectors(buffer);

        List<AttributeSet> liste = new ArrayList<>();

        Graph graph1 = new Graph(graphNodes1, sectors1, edges1, liste);

        Edge edge1 = Edge.of(graph1, 0, 0, 1);

        PointCh pointToTest = new PointCh(2600000, 1085000);

        System.out.println(edge1.positionClosestTo(pointToTest));
    }*/

    @Test
    void EdgeIsFineFinal() {
        IntBuffer forNodes = IntBuffer.wrap(new int[]{
                2_495_000 << 4,
                1_197_000 << 4,
                0x2_800_0015,
                2_657_112 << 4,
                1_080_000 << 4,
                0x2_674_0215
        });
        GraphNodes graphNodes1 = new GraphNodes(forNodes);
        ByteBuffer edgesBuffer = ByteBuffer.allocate(10);
        // Sens : inversé. Nœud destination : 12.
        edgesBuffer.putInt(0, 12);
        //Longueur : 0x10.b m (= 16.6875 m)
        edgesBuffer.putShort(4, (short) 0x10_b);
        // Dénivelé : 0x10.0 m (= 16.0 m)
        edgesBuffer.putShort(6, (short) 0x10_0);
        // Identité de l'ensemble d'attributs OSM : 1
        edgesBuffer.putShort(8, (short) 2022);
        IntBuffer profileIds = IntBuffer.wrap(new int[]{
                // Type : 0. Index du premier échantillon : 1.
                (3 << 30) | 1
        });
        ShortBuffer elevations = ShortBuffer.wrap(new short[]{
                (short) 0,
                (short) 0x180C, (short) 0xFEFF,
                (short) 0xFFFE, (short) 0xF000 //Type3
        });
        GraphEdges edges1 =
                new GraphEdges(edgesBuffer, profileIds, elevations);
        float[] type3Array = new float[]{
                384.75f, 384.6875f, 384.5625f, 384.5f, 384.4375f,
                384.375f, 384.3125f, 384.25f, 384.125f, 384.0625f,
        };
        ByteBuffer buffer = ByteBuffer.wrap(new byte[]{
                0, 0, 0, 12, 0, 10});
        GraphSectors sectors1 = new GraphSectors(buffer);
        List<AttributeSet> liste = new ArrayList<>();
        Graph graph1 = new Graph(graphNodes1, sectors1, edges1, liste);
        Edge edge1 = Edge.of(graph1, 0, 0, 1);
        PointCh pointToTest = new PointCh(2600000, 1085000);
        System.out.println(edge1.positionClosestTo(pointToTest));
    }
}


