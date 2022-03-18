package ch.epfl.javelo.routing;
import ch.epfl.javelo.Functions;
import ch.epfl.javelo.Math2;
import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.Ch1903;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.DoubleSummaryStatistics;
import java.util.function.DoubleUnaryOperator;

import static org.junit.jupiter.api.Assertions.*;

public class EdgeTest {
    static double DELTA = 1e-2;
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
}


