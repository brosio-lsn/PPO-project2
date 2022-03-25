package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.Math2;
import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.Ch1903;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.LongBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.DoubleUnaryOperator;

import static org.junit.jupiter.api.Assertions.*;

class SingleRouteTest {

    @Test
    void indexOfSegmentAt() {
        assertThrows(IllegalArgumentException.class, () -> {
            SingleRoute single = new SingleRoute(new ArrayList<>());
        });
    }

    @Test
    void length() {
        float[] yeet = {30f, 31f, 30.5f, 34f, 29f, 30f, 35f, 29.5f, 33f, 31f, 60f};
        PointCh fromPoint = new PointCh(SwissBounds.MIN_E + 500, SwissBounds.MIN_N + 200);
        PointCh toPoint = new PointCh(SwissBounds.MIN_E + 510, SwissBounds.MIN_N + 205);
        double length = Math2.norm((toPoint.n() - fromPoint.n()), (toPoint.e() - fromPoint.e()));
        DoubleUnaryOperator yo = Functions.sampled(yeet, length);
        Edge edge1 = new Edge(0, 50, fromPoint, toPoint, length, yo);

        float[] yeet2 = {28f, 30f, 30.5f, 34f, 26f, 30f, 37f, 29.5f, 32f, 31f, 80f};
        PointCh fromPoint2 = new PointCh(SwissBounds.MIN_E + 510, SwissBounds.MIN_N + 205);
        PointCh toPoint2 = new PointCh(SwissBounds.MIN_E + 522, SwissBounds.MIN_N + 211);
        double length2 = Math2.norm((toPoint2.n() - fromPoint2.n()), (toPoint2.e() - fromPoint2.e()));
        DoubleUnaryOperator yo2 = Functions.sampled(yeet2, length2);
        Edge edge2 = new Edge(51, 62, fromPoint2, toPoint2, length2, yo2);

        ArrayList<Edge> list = new ArrayList<>();
        list.add(edge1);
        list.add(edge2);
        SingleRoute single = new SingleRoute(list);
        assertEquals(length + length2, single.length(), 10E-2);
    }

    @Test
    void edges() {
        float[] yeet = {30f, 31f, 30.5f, 34f, 29f, 30f, 35f, 29.5f, 33f, 31f, 60f};
        PointCh fromPoint = new PointCh(SwissBounds.MIN_E + 500, SwissBounds.MIN_N + 200);
        PointCh toPoint = new PointCh(SwissBounds.MIN_E + 510, SwissBounds.MIN_N + 205);
        double length = Math2.norm((toPoint.n() - fromPoint.n()), (toPoint.e() - fromPoint.e()));
        DoubleUnaryOperator yo = Functions.sampled(yeet, length);
        Edge edge1 = new Edge(0, 50, fromPoint, toPoint, length, yo);

        float[] yeet2 = {28f, 30f, 30.5f, 34f, 26f, 30f, 37f, 29.5f, 32f, 31f, 80f};
        PointCh fromPoint2 = new PointCh(SwissBounds.MIN_E + 510, SwissBounds.MIN_N + 205);
        PointCh toPoint2 = new PointCh(SwissBounds.MIN_E + 522, SwissBounds.MIN_N + 211);
        double length2 = Math2.norm((toPoint2.n() - fromPoint2.n()), (toPoint2.e() - fromPoint2.e()));
        DoubleUnaryOperator yo2 = Functions.sampled(yeet2, length2);
        Edge edge2 = new Edge(51, 62, fromPoint2, toPoint2, length2, yo2);

        ArrayList<Edge> list = new ArrayList<>();
        list.add(edge1);
        list.add(edge2);
        SingleRoute single = new SingleRoute(list);
        assertEquals(list, single.edges());
        assertThrows(UnsupportedOperationException.class, () -> {
            single.edges().add(edge1);
            int b = 0;
        });
    }

    @Test
    void points() {
        float[] yeet = {30f, 31f, 30.5f, 34f, 29f, 30f, 35f, 29.5f, 33f, 31f, 60f};
        PointCh fromPoint = new PointCh(SwissBounds.MIN_E + 500, SwissBounds.MIN_N + 200);
        PointCh toPoint = new PointCh(SwissBounds.MIN_E + 510, SwissBounds.MIN_N + 205);
        double length = Math2.norm((toPoint.n() - fromPoint.n()), (toPoint.e() - fromPoint.e()));
        DoubleUnaryOperator yo = Functions.sampled(yeet, length);
        Edge edge1 = new Edge(0, 50, fromPoint, toPoint, length, yo);

        float[] yeet2 = {28f, 30f, 30.5f, 34f, 26f, 30f, 37f, 29.5f, 32f, 31f, 80f};
        PointCh fromPoint2 = new PointCh(SwissBounds.MIN_E + 510, SwissBounds.MIN_N + 205);
        PointCh toPoint2 = new PointCh(SwissBounds.MIN_E + 522, SwissBounds.MIN_N + 211);
        double length2 = Math2.norm((toPoint2.n() - fromPoint2.n()), (toPoint2.e() - fromPoint2.e()));
        DoubleUnaryOperator yo2 = Functions.sampled(yeet2, length2);
        Edge edge2 = new Edge(51, 62, fromPoint2, toPoint2, length2, yo2);

        ArrayList<PointCh> points = new ArrayList<>();
        points.add(fromPoint);
        points.add(fromPoint2);
        points.add(toPoint2);

        ArrayList<Edge> list = new ArrayList<>();
        list.add(edge1);
        list.add(edge2);
        SingleRoute single = new SingleRoute(list);

        assertEquals(points, single.points());
        /*assertThrows(UnsupportedOperationException.class, () ->{
            List<PointCh> ps = single.points();
        });*/

    }

    @Test
    void pointAt() {
        float[] yeet = {30f, 31f, 30.5f, 34f, 29f, 30f, 35f, 29.5f, 33f, 31f, 60f};
        PointCh fromPoint = new PointCh(SwissBounds.MIN_E + 500, SwissBounds.MIN_N + 200);
        PointCh toPoint = new PointCh(SwissBounds.MIN_E + 510, SwissBounds.MIN_N + 205);
        double length = Math2.norm((toPoint.n() - fromPoint.n()), (toPoint.e() - fromPoint.e()));
        DoubleUnaryOperator yo = Functions.sampled(yeet, length);
        Edge edge1 = new Edge(0, 50, fromPoint, toPoint, length, yo);

        float[] yeet2 = {28f, 30f, 30.5f, 34f, 26f, 30f, 37f, 29.5f, 32f, 31f, 80f};
        PointCh fromPoint2 = new PointCh(SwissBounds.MIN_E + 510, SwissBounds.MIN_N + 205);
        PointCh toPoint2 = new PointCh(SwissBounds.MIN_E + 522, SwissBounds.MIN_N + 211);
        double length2 = Math2.norm((toPoint2.n() - fromPoint2.n()), (toPoint2.e() - fromPoint2.e()));
        DoubleUnaryOperator yo2 = Functions.sampled(yeet2, length2);
        Edge edge2 = new Edge(51, 62, fromPoint2, toPoint2, length2, yo2);


        ArrayList<Edge> list = new ArrayList<>();
        list.add(edge1);
        list.add(edge2);
        SingleRoute single = new SingleRoute(list);

        assertEquals(edge1.pointAt(5.5), single.pointAt(5.5));
        assertEquals(edge2.pointAt(2), single.pointAt(length + 2));
        assertEquals(edge2.pointAt(0), single.pointAt(length));
        assertEquals(edge2.pointAt(length2), single.pointAt(length + length2));
        assertEquals(edge1.pointAt(0), single.pointAt(0));
        assertEquals(edge1.pointAt(length), single.pointAt(length));
        //assertEquals(fromPoint, single.pointAt(-10));
        single.pointAt(length + length2 + 1);
        assertEquals(edge2.pointAt(length2), single.pointAt(length * 40));
        assertEquals(edge1.pointAt(0), single.pointAt(-10));
    }

    @Test
    void nodeClosestTo() {
        float[] yeet = {30f, 31f, 30.5f, 34f, 29f, 30f, 35f, 29.5f, 33f, 31f, 60f};
        PointCh fromPoint = new PointCh(SwissBounds.MIN_E + 500, SwissBounds.MIN_N + 200);
        PointCh toPoint = new PointCh(SwissBounds.MIN_E + 510, SwissBounds.MIN_N + 205);
        double length = Math2.norm((toPoint.n() - fromPoint.n()), (toPoint.e() - fromPoint.e()));
        DoubleUnaryOperator yo = Functions.sampled(yeet, length);
        Edge edge1 = new Edge(0, 50, fromPoint, toPoint, length, yo);

        float[] yeet2 = {28f, 30f, 30.5f, 34f, 26f, 30f, 37f, 29.5f, 32f, 31f, 80f};
        PointCh fromPoint2 = new PointCh(SwissBounds.MIN_E + 510, SwissBounds.MIN_N + 205);
        PointCh toPoint2 = new PointCh(SwissBounds.MIN_E + 522, SwissBounds.MIN_N + 211);
        double length2 = Math2.norm((toPoint2.n() - fromPoint2.n()), (toPoint2.e() - fromPoint2.e()));
        DoubleUnaryOperator yo2 = Functions.sampled(yeet2, length2);
        Edge edge2 = new Edge(50, 62, fromPoint2, toPoint2, length2, yo2);


        ArrayList<Edge> list = new ArrayList<>();
        list.add(edge1);
        list.add(edge2);
        SingleRoute single = new SingleRoute(list);

        assertEquals(0, single.nodeClosestTo(1.5));
        assertEquals(50, single.nodeClosestTo(length));
        assertEquals(50, single.nodeClosestTo(length - 1));
        assertEquals(50, single.nodeClosestTo(length + 1));
        assertEquals(62, single.nodeClosestTo(length + length2 - 1));
        assertEquals(0, single.nodeClosestTo(-10));
        assertEquals(62, single.nodeClosestTo(5000));
        assertEquals(0, single.nodeClosestTo(length / 2));
        single.nodeClosestTo(length + length2);
        assertEquals(62, single.nodeClosestTo(length + length2));
        assertEquals(0, single.nodeClosestTo(-100));
        //single.nodeClosestTo(length+length2);
        assertEquals(62, single.nodeClosestTo(length + length2 + 6));
    }

    @Test
    void pointClosestTo() {
        float[] yeet = {30f, 31f, 30.5f, 34f, 29f, 30f, 35f, 29.5f, 33f, 31f, 60f};
        PointCh fromPoint = new PointCh(SwissBounds.MIN_E + 500, SwissBounds.MIN_N + 200);
        PointCh toPoint = new PointCh(SwissBounds.MIN_E + 510, SwissBounds.MIN_N + 205);
        double length = Math2.norm((toPoint.n() - fromPoint.n()), (toPoint.e() - fromPoint.e()));
        DoubleUnaryOperator yo = Functions.sampled(yeet, length);
        Edge edge1 = new Edge(0, 50, fromPoint, toPoint, length, yo);

        float[] yeet2 = {28f, 30f, 30.5f, 34f, 26f, 30f, 37f, 29.5f, 32f, 31f, 80f};
        PointCh fromPoint2 = new PointCh(SwissBounds.MIN_E + 510, SwissBounds.MIN_N + 205);
        PointCh toPoint2 = new PointCh(SwissBounds.MIN_E + 522, SwissBounds.MIN_N + 211);
        double length2 = Math2.norm((toPoint2.n() - fromPoint2.n()), (toPoint2.e() - fromPoint2.e()));
        DoubleUnaryOperator yo2 = Functions.sampled(yeet2, length2);
        Edge edge2 = new Edge(50, 62, fromPoint2, toPoint2, length2, yo2);


        ArrayList<Edge> list = new ArrayList<>();
        list.add(edge1);
        list.add(edge2);
        SingleRoute single = new SingleRoute(list);
        single.pointClosestTo(new PointCh(SwissBounds.MIN_E + 400, SwissBounds.MIN_N + 190));
        /*System.out.println(fromPoint.distanceTo(new PointCh(SwissBounds.MIN_E+400, SwissBounds.MIN_N+190)));
        assertEquals(fromPoint, single.pointClosestTo(new PointCh(SwissBounds.MIN_E+400, SwissBounds.MIN_N+190)));*/
        /*System.out.println(toPoint2.distanceTo(new PointCh(SwissBounds.MIN_E+600, SwissBounds.MIN_N+700)));
        assertEquals(toPoint2, single.pointClosestTo(new PointCh(SwissBounds.MIN_E+600, SwissBounds.MIN_N+700)));*/
        /*System.out.println(0);
        assertEquals(toPoint, single.pointClosestTo(toPoint));*/
        /*System.out.println(single.length());
        assertEquals(toPoint2, single.pointClosestTo(toPoint2));*/
        /*System.out.println(toPoint2.distanceTo(new PointCh(SwissBounds.MIN_E+501, SwissBounds.MIN_N+207)));
        assertEquals(edge1.pointAt( edge1.positionClosestTo(new PointCh(SwissBounds.MIN_E+501, SwissBounds.MIN_N+207))), single.pointClosestTo(new PointCh(SwissBounds.MIN_E+501, SwissBounds.MIN_N+207)));*/
        /*System.out.println(edge2.pointAt( edge2.positionClosestTo(new PointCh(SwissBounds.MIN_E+519, SwissBounds.MIN_N+210))).distanceTo(new PointCh(SwissBounds.MIN_E+519, SwissBounds.MIN_N+210)));
        assertEquals(edge2.pointAt( edge2.positionClosestTo(new PointCh(SwissBounds.MIN_E+519, SwissBounds.MIN_N+210))), single.pointClosestTo(new PointCh(SwissBounds.MIN_E+519, SwissBounds.MIN_N+210)));*/
        /*System.out.println(edge2.pointAt( edge2.positionClosestTo(new PointCh(SwissBounds.MIN_E+519.7, SwissBounds.MIN_N+210.9))).distanceTo(new PointCh(SwissBounds.MIN_E+519.7, SwissBounds.MIN_N+210.9)));
        assertEquals(edge2.pointAt( edge2.positionClosestTo(new PointCh(SwissBounds.MIN_E+519.7, SwissBounds.MIN_N+210.9))), single.pointClosestTo(new PointCh(SwissBounds.MIN_E+519.7, SwissBounds.MIN_N+210.9)));*/
        System.out.println(edge2.pointAt(edge2.positionClosestTo(new PointCh(SwissBounds.MIN_E + 503.6, SwissBounds.MIN_N + 206.9))).distanceTo(new PointCh(SwissBounds.MIN_E + 503.6, SwissBounds.MIN_N + 206.9)));
        assertEquals(edge2.pointAt(edge2.positionClosestTo(new PointCh(SwissBounds.MIN_E + 503.6, SwissBounds.MIN_N + 206.9))), single.pointClosestTo(new PointCh(SwissBounds.MIN_E + 503.6, SwissBounds.MIN_N + 206.9)));


    }

    @Test
    void elevationAt() {

        float[] yeet = {30f, 31f, 30.5f, 34f, 29f, 30f, 35f, 29.5f, 33f, 31f, 60f};
        PointCh fromPoint = new PointCh(SwissBounds.MIN_E + 500, SwissBounds.MIN_N + 200);
        PointCh toPoint = new PointCh(SwissBounds.MIN_E + 510, SwissBounds.MIN_N + 205);
        double length = Math2.norm((toPoint.n() - fromPoint.n()), (toPoint.e() - fromPoint.e()));
        DoubleUnaryOperator yo = Functions.sampled(yeet, length);
        Edge edge1 = new Edge(0, 50, fromPoint, toPoint, length, yo);

        float[] yeet2 = {28f, 30f, 30.5f, 34f, 26f, 30f, 37f, 29.5f, 32f, 31f, 80f};
        PointCh fromPoint2 = new PointCh(SwissBounds.MIN_E + 510, SwissBounds.MIN_N + 205);
        PointCh toPoint2 = new PointCh(SwissBounds.MIN_E + 522, SwissBounds.MIN_N + 211);
        double length2 = Math2.norm((toPoint2.n() - fromPoint2.n()), (toPoint2.e() - fromPoint2.e()));
        DoubleUnaryOperator yo2 = Functions.sampled(yeet2, length2);
        Edge edge2 = new Edge(50, 62, fromPoint2, toPoint2, length2, yo2);
        ArrayList<Edge> list = new ArrayList<>();
        list.add(edge1);
        list.add(edge2);
        SingleRoute single = new SingleRoute(list);
        assertEquals(edge1.elevationAt(1), single.elevationAt(1));
        assertEquals(edge1.elevationAt(length - 1), single.elevationAt(length - 1));
        assertEquals(edge2.elevationAt(1), single.elevationAt(length + 1));
        assertEquals(edge2.elevationAt(0), single.elevationAt(length));
        assertEquals(edge2.elevationAt(length2 - 1), single.elevationAt(length + length2 - 1));
        assertEquals(edge1.elevationAt(-1), single.elevationAt(-10));
        assertEquals(edge2.elevationAt(60000), single.elevationAt(5000));
        assertEquals(edge1.elevationAt(length / 2), single.elevationAt(length / 2));
        assertEquals(30f, single.elevationAt(0));
        single.elevationAt(length + length2);
        single.elevationAt(length + length2);
        assertEquals(80f, single.elevationAt(length + length2));
        assertEquals(80f, single.elevationAt(length + length2 + 6));
        assertEquals(30f, single.elevationAt(-100));
    }

    private final double DELTA = 1e-7;
    Path filePath = Path.of("lausanne");
    Graph e = Graph.loadFrom(filePath);


    private SingleRouteTest() throws IOException {
    }

    @Test
    void emptyListThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> {
            SingleRoute a = new SingleRoute(new ArrayList<Edge>());
        });
    }

    @Test
    void ListDoesThrowsIllegalArgumentException() throws IOException {
        List<Edge> edges = new ArrayList<Edge>();
        edges.add(Edge.of(e, 0, 1, 2));
        assertDoesNotThrow(() -> {
            new SingleRoute(edges);
        });
    }

    @Test
    void testIndexSegmentAt() {
        List<Edge> edges = new ArrayList<Edge>();
        edges.add(Edge.of(e, 0, 1, 2));
        SingleRoute a = new SingleRoute(edges);
        assertEquals(0, a.indexOfSegmentAt(1000000000));
    }

    @Test
    void testLength() {
        List<Edge> edges = new ArrayList<Edge>();
        edges.add(Edge.of(e, 232379, 109351, 109356));
        edges.add(Edge.of(e, 232380, 109349, 109353));
        Edge b = Edge.of(e, 232379, 109351, 109356);
        Edge f = (Edge.of(e, 232380, 109349, 109353));
        System.out.println(b.length());
        System.out.println(f.length());
        SingleRoute a = new SingleRoute(edges);

        List<Edge> edges1 = new ArrayList<Edge>();
        edges1.add(Edge.of(e, 0, 90000, 90000));
        SingleRoute a1 = new SingleRoute(edges1);


        List<Edge> edges2 = new ArrayList<Edge>();
        edges2.add(b);
        SingleRoute a2 = new SingleRoute(edges2);


        assertEquals(3.9375, a.length());
        assertEquals(95.125, a1.length());
        assertEquals(1.9375, a2.length());


    }


    @Test
    void pointsTest() throws IOException {

        List<Edge> edges = new ArrayList<Edge>();
        edges.add(Edge.of(e, 232379, 109351, 109356));
        edges.add(Edge.of(e, 232380, 109349, 109353));

        System.out.println("hey");
        System.out.println(e.edgeTargetNodeId(232379));//109349
        //109351
        System.out.println(e.edgeTargetNodeId(232380));

        PointCh pointfromNodeId = new PointCh(2537498.6875, 1154426.4375);
        PointCh pointToNodeId = new PointCh(2537497.625, 1154426.4375);
        PointCh pointToNodeId2 = new PointCh(2537495.1875, 1154425.4375);
        PointCh pointToNodeId3 = new PointCh(2537500.6875, 1154426.375);
        List<PointCh> pointsExpected = new ArrayList<>();
        pointsExpected.add(pointfromNodeId);
        pointsExpected.add(pointToNodeId);
        pointsExpected.add(pointToNodeId2);
        pointsExpected.add(pointToNodeId3);

        PointCh testing = new PointCh(2537497.625, 1154439.125);
        PointCh testing2 = new PointCh(2537500.6875, 1154426.375);
        System.out.println("starting");
        System.out.println(Math.toDegrees(pointfromNodeId.lat()));
        System.out.println(Math.toDegrees(pointfromNodeId.lon()));
        System.out.println(Math.toDegrees(testing.lat()));
        System.out.println(Math.toDegrees(testing.lon()));
        System.out.println(Math.toDegrees(testing2.lat()));
        System.out.println(Math.toDegrees(testing2.lon()));


        SingleRoute route1 = new SingleRoute(edges);

        Edge ed1 = Edge.of(e, 287113, 134119, 133636); //chemin de roseneck
        Edge ed2 = Edge.of(e, 285998, 133636, 133953);//chemin de beau rivage vers le bas
        System.out.println(e.nodeOutDegree(133636));
        System.out.println(e.nodeOutEdgeId(133636, 0));
        System.out.println(e.nodeOutEdgeId(133636, 1));
        System.out.println(e.nodeOutEdgeId(133636, 2));
        System.out.println(e.edgeTargetNodeId(285997));
        System.out.println(e.edgeTargetNodeId(285998));
        System.out.println(e.edgeTargetNodeId(285999));
        List<Edge> edges2 = new ArrayList<Edge>();
        edges2.add(ed1);
        edges2.add(ed2);
        SingleRoute a1 = new SingleRoute(edges2);
        List<PointCh> pointsExpected2 = new ArrayList<>();
        pointsExpected2.add(e.nodePoint(134119));
        pointsExpected2.add(e.nodePoint(133636));
        pointsExpected2.add(e.nodePoint(133953));
        assertEquals(pointsExpected2, a1.points());
        //assertEquals(pointsExpected, route1.points());


    }


    @Test
    void pointAtOnNodeTest() {
        Edge ed1 = Edge.of(e, 287113, 134119, 133636); //chemin de roseneck
        Edge ed2 = Edge.of(e, 285998, 133636, 133953);
        List<Edge> edges2 = new ArrayList<Edge>();
        edges2.add(ed1);
        edges2.add(ed2);
        SingleRoute a1 = new SingleRoute(edges2);
        System.out.println(ed1.length());
        a1.pointAt(74.875);
        PointCh expected = e.nodePoint(133636);
        PointCh expected2 = e.nodePoint(134119);
        PointCh expected3 = e.nodePoint(133953);
        System.out.println(a1.length());
        assertEquals(expected, a1.pointAt(74.875));
        assertEquals(expected2, a1.pointAt(0));
        assertEquals(expected3, a1.pointAt(110.6875));

    }

    @Test
    void pointAtOutNodeTest() {
        Edge ed1 = Edge.of(e, 287113, 134119, 133636); //chemin de roseneck
        Edge ed2 = Edge.of(e, 285998, 133636, 133953);
        List<Edge> edges = new ArrayList<Edge>();
        edges.add(ed1);
        edges.add(ed2);
        System.out.println(ed1.length());
        System.out.println(ed2.length());

        SingleRoute a1 = new SingleRoute(edges);
        System.out.println(a1.length());
        System.out.println(a1.pointAt(200));
        PointCh expected = ed1.pointAt(60);
        PointCh expected2 = ed2.pointAt(20);
        PointCh expected3 = ed2.pointAt(35.8125);
        PointCh expected4 = ed1.pointAt(0);
        assertEquals(expected, a1.pointAt(60));
        assertEquals(expected2, a1.pointAt(94.875));
        assertEquals(expected3, ed2.toPoint());
        assertEquals(ed2.toPoint(), a1.pointAt(200));
        assertEquals(expected4, a1.pointAt(-50));


    }

    @Test
    void elevationAtOnNodes() {


        //todo chelou node du milieu même point mais pas même profil
        Edge ed1 = Edge.of(e, 287113, 134119, 133636); //chemin de roseneck
        Edge ed2 = Edge.of(e, 285998, 133636, 133953);
        List<Edge> edges2 = new ArrayList<Edge>();
        edges2.add(ed1);
        edges2.add(ed2);
        SingleRoute a1 = new SingleRoute(edges2);
        System.out.println(ed1.length());
        a1.pointAt(74.875);
        double expected = ed1.elevationAt(0);
        double expected2 = ed2.elevationAt(0);
        System.out.println(ed1.pointAt(74.875));
        System.out.println(ed2.pointAt(0));
        System.out.println(ed2.elevationAt(0));
        System.out.println(ed1.elevationAt(74.875));
        double expected3 = ed2.elevationAt(35.8125);
        assertEquals(expected, a1.elevationAt(0));
        assertEquals(expected2, a1.elevationAt(74.875));
        assertEquals(expected3, a1.elevationAt(110.6875));
    }

    @Test
    void elevationAtOutNodes() {


        //todo node du milieu même point mais pas même profil
        Edge ed1 = Edge.of(e, 287113, 134119, 133636); //chemin de roseneck
        Edge ed2 = Edge.of(e, 285998, 133636, 133953);
        List<Edge> edges2 = new ArrayList<Edge>();
        edges2.add(ed1);
        edges2.add(ed2);
        SingleRoute a1 = new SingleRoute(edges2);
        System.out.println(ed1.length());
        System.out.println(ed2.length());
        a1.pointAt(74.875);
        System.out.println(ed1.elevationAt(74.875));
        System.out.println(ed2.pointAt(0));
        System.out.println(ed1.pointAt(74.875));
        System.out.println(ed2.elevationAt(0));
        double expected = ed1.elevationAt(60);
        double expected2 = ed2.elevationAt(20);
        double expected3 = ed2.elevationAt(35.8125);
        double expected4 = ed1.elevationAt(0);
        double expected5 = ed1.elevationAt(ed1.length());
        double expected6 = ed2.elevationAt(0);
        assertEquals(expected, a1.elevationAt(60));
        assertEquals(expected2, a1.elevationAt(94.875));
        assertEquals(expected3, a1.elevationAt(200));
        assertEquals(expected4, a1.elevationAt(-50));
        //assertEquals(expected5,a1.elevationAt(74.875));
        assertEquals(expected6, a1.elevationAt(74.875));
        //assertEquals(expected5,expected6);
    }

    @Test
    void nodeClosestToTest() {

        //todo check toNodeId from NodeId?
        Edge ed1 = Edge.of(e, 287113, 134119, 133636); //chemin de roseneck
        Edge ed2 = Edge.of(e, 285998, 133636, 133953);
        List<Edge> edges2 = new ArrayList<Edge>();
        edges2.add(ed1);
        edges2.add(ed2);
        SingleRoute a1 = new SingleRoute(edges2);
        double expected1 = 134119;
        assertEquals(expected1, a1.nodeClosestTo(0.8));
        assertEquals(133636, a1.nodeClosestTo(70));
        assertEquals(133636, a1.nodeClosestTo(76));
        assertEquals(134119, a1.nodeClosestTo(0));
        assertEquals(133636, a1.nodeClosestTo(74.875));
        assertEquals(133953, a1.nodeClosestTo(110.6875));
        assertEquals(133953, a1.nodeClosestTo(2000));
        assertEquals(133953, a1.nodeClosestTo(108));
        assertEquals(134119, a1.nodeClosestTo(-90));
        assertEquals(134119, a1.nodeClosestTo(37.4375));
    }

    @Test
    void pointClosestToTest() {
        Edge ed1 = Edge.of(e, 287113, 134119, 133636); //chemin de roseneck
        Edge ed2 = Edge.of(e, 285998, 133636, 133953);
        System.out.println("fromPoint ed1 " + ed1.fromPoint());
        System.out.println("toPoint ed1 " + ed1.toPoint());
        System.out.println("fromPointed2" + ed2.fromPoint());
        System.out.println("toPointed2" + ed2.toPoint());
        List<Edge> edges2 = new ArrayList<Edge>();
        edges2.add(ed1);
        edges2.add(ed2);
        //System.out.println(ed1.length());
        //System.out.println(ed2.length());
        SingleRoute a1 = new SingleRoute(edges2);
        PointCh firstNode = new PointCh(2537789.875, 1151256.3125);
        PointCh secondNode = new PointCh(2537936.0625, 1151212.9375);
        PointCh thirdNode = new PointCh(2537928.625, 1151178.0625);
        //System.out.println(ed2.positionClosestTo(thirdNode));
        //assertEquals(ed2.toPoint(),thirdNode);

        //assertEquals(35.8125, ed2.positionClosestTo(ed2.toPoint()));


        assertEquals(new RoutePoint(firstNode, 0, 0), a1.pointClosestTo(firstNode));
        assertEquals(new RoutePoint(secondNode, 74.875, 0), a1.pointClosestTo(secondNode));
        double position = ed2.positionClosestTo(ed2.toPoint());
        PointCh Expected = ed2.pointAt(position);
        double distance = Expected.distanceTo(ed2.toPoint());
        assertEquals(new RoutePoint(Expected, position + ed1.length(), distance), a1.pointClosestTo(ed2.toPoint()));


        //46.509838, 6.629164
        double coordinateE2 = Ch1903.e(Math.toRadians(6.629164), Math.toRadians(46.509838));
        double coordinateN2 = Ch1903.n(Math.toRadians(6.629164), Math.toRadians(46.509838));
        PointCh point2 = new PointCh(coordinateE2, coordinateN2);
        PointCh an1 = new PointCh(2537936.0625, 1151212.9375);
        System.out.println(Math.toDegrees(an1.lat()));
        System.out.println(Math.toDegrees(an1.lon()));
        RoutePoint routepoint2 = a1.pointClosestTo(point2);
        double position1 = Math2.clamp(0, ed1.positionClosestTo(point2), 74.875);
        PointCh Expected1 = ed1.pointAt(position1);
        double distance1 = Expected1.distanceTo(point2);
        assertEquals(ed2.pointAt(0), ed1.pointAt(ed1.length()));
        assertEquals(new RoutePoint(Expected1, position1, distance1), routepoint2);

        //PointCh[e=2537915.807002296, n=1151218.9474681087]





        /*
        //46.509725, 6.627964 //cohérent
        double coordinateE = Ch1903.e(Math.toRadians(6.627964), Math.toRadians(46.509725));
        double coordinateN = Ch1903.n(Math.toRadians(6.627964), Math.toRadians(46.509725));
        PointCh point = new PointCh(coordinateE, coordinateN);
        PointCh an = new PointCh(2537789.875, 1151256.3125);
        System.out.println(Math.toDegrees(an.lat()));
        System.out.println(Math.toDegrees(an.lon()));

        RoutePoint routepoint = a1.pointClosestTo(point);
        System.out.println(routepoint);

         */


        //46.509690, 6.628953
        double coordinateE1 = Ch1903.e(Math.toRadians(6.628953), Math.toRadians(46.509690));
        double coordinateN1 = Ch1903.n(Math.toRadians(6.628953), Math.toRadians(46.509690));
        PointCh point1 = new PointCh(coordinateE1, coordinateN1);
        System.out.println(point1);
        PointCh a = new PointCh(2537915.807002296, 1151218.9474681087);
        System.out.println(Math.toDegrees(a.lat()));
        System.out.println(Math.toDegrees(a.lon()));

        RoutePoint routepoint1 = a1.pointClosestTo(point1);
        double position2 = Math2.clamp(0, ed1.positionClosestTo(point1), 74.875);
        PointCh Expected2 = ed1.pointAt(position2);
        double distance2 = Expected2.distanceTo(point1);
        assertEquals(new RoutePoint(Expected2, position2, distance2), routepoint1);


        //46.508768, 6.630354
        /*fromPoint ed1 PointCh[e=2537789.875, n=1151256.3125]
        toPoint ed1 PointCh[e=2537936.0625, n=1151212.9375]
        fromPointed2PointCh[e=2537936.0625, n=1151212.9375]
        toPointed2PointCh[e=2537928.625, n=1151178.0625]
        point [e=2537856.189372507, n=1151253.0589264273]
        résultat : 2537915.807002296, 1151218.9474681087
         */
        /*
        double coordinateE3 = Ch1903.e(Math.toRadians(6.628953), Math.toRadians(46.509690));
        double coordinateN3 = Ch1903.n(Math.toRadians(6.628953), Math.toRadians(46.509690));
        PointCh point3 = new PointCh(coordinateE3, coordinateN3);
        System.out.println(point3);
        PointCh an3 = new PointCh(2537915.807002296, 1151218.9474681087);
        System.out.println(Math.toDegrees(an3.lat()));
        System.out.println(Math.toDegrees(an3.lon()));

        RoutePoint routepoint1 = a1.pointClosestTo(point3);
        System.out.println(routepoint1);


         */

        /*
        //46.509181, 6.628831
        double coordinateE4 = Ch1903.e(Math.toRadians(6.628831), Math.toRadians(46.509181));
        double coordinateN4 = Ch1903.n(Math.toRadians(6.628831), Math.toRadians(46.509181));
        PointCh point4 = new PointCh(coordinateE4, coordinateN4);
        System.out.println(point4);
        PointCh an4 = new PointCh(2537928.555288018, 1151215.164949814);
        System.out.println(Math.toDegrees(an4.lat()));
        System.out.println(Math.toDegrees(an4.lon()));

        RoutePoint routepoint4 = a1.pointClosestTo(point4);
        System.out.println(routepoint4);

         */

        //46.507877, 6.636108
        double coordinateE5 = Ch1903.e(Math.toRadians(6.628831), Math.toRadians(46.509181));
        double coordinateN5 = Ch1903.n(Math.toRadians(6.628831), Math.toRadians(46.509181));
        PointCh point5 = new PointCh(coordinateE5, coordinateN5);
        System.out.println(point5);
        PointCh an5 = new PointCh(2537928.555288018, 1151215.164949814);
        System.out.println(Math.toDegrees(an5.lat()));
        System.out.println(Math.toDegrees(an5.lon()));

        RoutePoint routepoint5 = a1.pointClosestTo(point5);
        System.out.println(routepoint5);


    }

    @Test
    void testOnlyWithOne() {

        Edge ed1 = Edge.of(e, 287113, 134119, 133636);
        List<Edge> edges2 = new ArrayList<Edge>();
        edges2.add(ed1);
        SingleRoute a1 = new SingleRoute(edges2);

        double coordinateE5 = Ch1903.e(Math.toRadians(6.628831), Math.toRadians(46.509181));
        double coordinateN5 = Ch1903.n(Math.toRadians(6.628831), Math.toRadians(46.509181));
        PointCh point5 = new PointCh(coordinateE5, coordinateN5);
        double expectedPosition = ed1.positionClosestTo(point5);
        PointCh onRouteExpected = ed1.pointAt(expectedPosition);
        double expectedDistance = onRouteExpected.distanceTo(point5);
        RoutePoint expectedRoutePoint = new RoutePoint(onRouteExpected, expectedPosition, expectedDistance);
        assertEquals(expectedRoutePoint, a1.pointClosestTo(point5));


    }


    @Test
    void findEdges() throws IOException {
        Path filePath = Path.of("lausanne/nodes_osmid.bin");
        LongBuffer osmIdBuffer;
        try (FileChannel channel = FileChannel.open(filePath)) {
            osmIdBuffer = channel
                    .map(FileChannel.MapMode.READ_ONLY, 0, channel.size())
                    .asLongBuffer();
        }
        System.out.println(osmIdBuffer.get(210000));
        System.out.println(osmIdBuffer.get(209775));
        System.out.println(osmIdBuffer.get(150000));
        System.out.println(osmIdBuffer.get(180000));
        System.out.println(osmIdBuffer.get(190000));
        System.out.println(osmIdBuffer.get(191227));
        System.out.println(osmIdBuffer.get(109350));//3389153419
        System.out.println(osmIdBuffer.get(134119));
        System.out.println(osmIdBuffer.get(134118));
        System.out.println(osmIdBuffer.get(134120));
        System.out.println(osmIdBuffer.get(134121));
        System.out.println(osmIdBuffer.get(209768));
        System.out.println(osmIdBuffer.get(209949));
        System.out.println(osmIdBuffer.get(209773));
        System.out.println(osmIdBuffer.get(194890));
        System.out.println(osmIdBuffer.get(109356));

        System.out.println(e.nodeOutDegree(109350));
        System.out.println(e.nodeOutEdgeId(109350, 0));//232379
        System.out.println(e.nodeOutEdgeId(109350, 1));//232380
        System.out.println(e.edgeAttributes(232379));
        System.out.println(e.edgeTargetNodeId(232379));
        System.out.println(e.edgeTargetNodeId(232380));
        System.out.println(osmIdBuffer.get(109351));
        System.out.println(osmIdBuffer.get(109349));
        System.out.println(e.edgeAttributes((232380)));
        //593969873
        System.out.println();

        int index = 0;
        for (int i = 0; i < osmIdBuffer.capacity(); i++) {
            if (osmIdBuffer.get(i) == 564464307L) {
                index = i;
            }


        }
        System.out.println("index" + index);


    }


    @Test
    void findEdges2() throws IOException {
        Path filePath = Path.of("lausanne/nodes_osmid.bin");
        LongBuffer osmIdBuffer;
        try (FileChannel channel = FileChannel.open(filePath)) {
            osmIdBuffer = channel
                    .map(FileChannel.MapMode.READ_ONLY, 0, channel.size())
                    .asLongBuffer();
        }

        System.out.println(osmIdBuffer.get(109351));
        System.out.println(osmIdBuffer.get(109356));
        System.out.println(osmIdBuffer.get(109349));
        System.out.println(osmIdBuffer.get(109353));
        System.out.println(osmIdBuffer.get(134119));//premier noeuds chemin de roseneck
        System.out.println(osmIdBuffer.get(133636));//deuxième noeud
        System.out.println(osmIdBuffer.get(133631));
        System.out.println(osmIdBuffer.get(133953));//chemin de beau rivage vers le bas
        System.out.println(osmIdBuffer.get(133637));

    }
    public static SingleRoute HorizontalPath() {
        PointCh point0 = new PointCh(2_532_700, 1_152_348);
        PointCh point1 = new PointCh(2_532_700 + 5800, 1_152_348);
        PointCh point2 = new PointCh(2_532_700 + 8100, 1_152_348);
        PointCh point3 = new PointCh(2_532_700 + 9200, 1_152_348);
        PointCh point4 = new PointCh(2_532_700 + 11400, 1_152_348);
        PointCh point5 = new PointCh(2_532_700 + 13100, 1_152_348);

        Edge edge0 = new Edge(0,1, point0, point1, 5800, DoubleUnaryOperator.identity());
        Edge edge1 = new Edge(1,2, point1, point2, 2300, DoubleUnaryOperator.identity());
        Edge edge2 = new Edge(2,3, point2, point3, 1100, DoubleUnaryOperator.identity());
        Edge edge3 = new Edge(3,4, point3, point4, 2200, DoubleUnaryOperator.identity());
        Edge edge4 = new Edge(4,5, point4, point5, 1700, DoubleUnaryOperator.identity());

        List<Edge> edgeList = new ArrayList<>(List.of(edge0, edge1, edge2, edge3, edge4));
        return new SingleRoute(edgeList);
    }

    public static SingleRoute VerticalPath() {
        PointCh point0 = new PointCh(2_532_705, 1_152_300);
        PointCh point1 = new PointCh(2_532_705, 1_152_300 + 5800);
        PointCh point2 = new PointCh(2_532_705, 1_152_300 + 8100);
        PointCh point3 = new PointCh(2_532_705, 1_152_300 + 9200);
        PointCh point4 = new PointCh(2_532_705, 1_152_300 + 11400);
        PointCh point5 = new PointCh(2_532_705, 1_152_300 + 13100);

        Edge edge0 = new Edge(0,1, point0, point1, 5800, DoubleUnaryOperator.identity());
        Edge edge1 = new Edge(1,2, point1, point2, 2300, DoubleUnaryOperator.identity());
        Edge edge2 = new Edge(2,3, point2, point3, 1100, DoubleUnaryOperator.identity());
        Edge edge3 = new Edge(3,4, point3, point4, 2200, DoubleUnaryOperator.identity());
        Edge edge4 = new Edge(4,5, point4, point5, 1700, DoubleUnaryOperator.identity());

        List<Edge> edgeList = new ArrayList<>(List.of(edge0, edge1, edge2, edge3, edge4));
        return new SingleRoute(edgeList);
    }

    public static SingleRoute DiagonalPath() {
        PointCh point0 = new PointCh(2_532_705, 1_152_348);
        PointCh point1 = new PointCh(2_532_715, 1_152_358);
        PointCh point2 = new PointCh(2_532_725, 1_152_368);
        PointCh point3 = new PointCh(2_532_735, 1_152_378);
        PointCh point4 = new PointCh(2_532_745, 1_152_388);
        PointCh point5 = new PointCh(2_532_755, 1_152_398);

        Edge edge0 = new Edge(0,1, point0, point1, Math.sqrt(200), DoubleUnaryOperator.identity());
        Edge edge1 = new Edge(1,2, point1, point2, Math.sqrt(200), DoubleUnaryOperator.identity());
        Edge edge2 = new Edge(2,3, point2, point3, Math.sqrt(200), DoubleUnaryOperator.identity());
        Edge edge3 = new Edge(3,4, point3, point4, Math.sqrt(200), DoubleUnaryOperator.identity());
        Edge edge4 = new Edge(4,5, point4, point5, Math.sqrt(200), DoubleUnaryOperator.identity());

        List<Edge> edgeList = new ArrayList<>(List.of(edge0, edge1, edge2, edge3, edge4));
        return new SingleRoute(edgeList);
    }


    public static SingleRoute ZebraPath() {
        PointCh point0 = new PointCh( 2485000, 1075000); //MIN E,N SwissBounds
        PointCh point1 = new PointCh(2485100, 1075000);
        PointCh point2 = new PointCh(2485100, 1075100);
        PointCh point3 = new PointCh(2485200, 1075100);
        PointCh point4 = new PointCh(2485200, 1075200);
        PointCh point5 = new PointCh(2485300, 1075200);

        Edge edge0 = new Edge(0, 1, point0, point1, 100, DoubleUnaryOperator.identity());
        Edge edge1 = new Edge(1, 2, point1, point2, 100, DoubleUnaryOperator.identity());
        Edge edge2 = new Edge(2, 3, point2, point3, 100, DoubleUnaryOperator.identity());
        Edge edge3 = new Edge(3, 4, point3, point4, 100, DoubleUnaryOperator.identity());
        Edge edge4 = new Edge(4, 5, point4, point5, 100, DoubleUnaryOperator.identity());

        List<Edge> edgeList = new ArrayList<>(List.of(edge0, edge1, edge2, edge3, edge4));
        return new SingleRoute(edgeList);
    }

    @Test
    void SingleRouteThrowsOnEmptyList() {
        List<Edge> emptyList = new ArrayList<>();
        assertThrows(
                IllegalArgumentException.class,
                () -> { new SingleRoute(emptyList); });
    }

    @Test
    void indexOfSegmentWorks() {
        int valH = HORIZONTAL_PATH.indexOfSegmentAt(42);
        int valV = VERTICAL_PATH.indexOfSegmentAt(24);
        int valD = DIAGONAL_PATH.indexOfSegmentAt(44);
        assertEquals(0, valH);
        assertEquals(0, valV);
        assertEquals(0, valD);
    }
    private static final SingleRoute HORIZONTAL_PATH = HorizontalPath(); // Constant north coordinate
    private static final SingleRoute VERTICAL_PATH = VerticalPath(); // Constant east coordinate
    private static final SingleRoute DIAGONAL_PATH = DiagonalPath(); //
    private static final SingleRoute ZEBRA_PATH = ZebraPath(); // Zebra path


    @Test
    void lengthHorizontalPathWorks() {
        double valH = HORIZONTAL_PATH.length();
        double valV = VERTICAL_PATH.length();
        double valD = DIAGONAL_PATH.length();
        assertEquals(13100, valH);
        assertEquals(13100, valV);
        assertEquals(Math.sqrt(200) * 5, valD);
    }

    @Test
    void edgesWorks() {
        assertEquals(HorizontalPath().edges(), HORIZONTAL_PATH.edges());
        assertEquals(VerticalPath().edges(), VERTICAL_PATH.edges());
        assertEquals(DiagonalPath().edges(), DIAGONAL_PATH.edges());
        //kind of stupid
    }

    @Test
    void pointsWorksHorizontal() {
        PointCh point0 = new PointCh(2_532_700, 1_152_348);
        PointCh point1 = new PointCh(2_532_700 + 5800, 1_152_348);
        PointCh point2 = new PointCh(2_532_700 + 8100, 1_152_348);
        PointCh point3 = new PointCh(2_532_700 + 9200, 1_152_348);
        PointCh point4 = new PointCh(2_532_700 + 11400, 1_152_348);
        PointCh point5 = new PointCh(2_532_700 + 13100, 1_152_348);

        List<PointCh> pointsList = new ArrayList<>(List.of(point0, point1, point2, point3, point4, point5));

        assertEquals(pointsList, HORIZONTAL_PATH.points());
    }

    @Test
    void pointsWorksVertical() {
        PointCh point0 = new PointCh(2_532_705, 1_152_300);
        PointCh point1 = new PointCh(2_532_705, 1_152_300 + 5800);
        PointCh point2 = new PointCh(2_532_705, 1_152_300 + 8100);
        PointCh point3 = new PointCh(2_532_705, 1_152_300 + 9200);
        PointCh point4 = new PointCh(2_532_705, 1_152_300 + 11400);
        PointCh point5 = new PointCh(2_532_705, 1_152_300 + 13100);

        List<PointCh> pointsList = new ArrayList<>(List.of(point0, point1, point2, point3, point4, point5));

        assertEquals(pointsList, VERTICAL_PATH.points());
    }

    @Test
    void pointsWorksDiagonal() {
        PointCh point0 = new PointCh(2_532_705, 1_152_348);
        PointCh point1 = new PointCh(2_532_715, 1_152_358);
        PointCh point2 = new PointCh(2_532_725, 1_152_368);
        PointCh point3 = new PointCh(2_532_735, 1_152_378);
        PointCh point4 = new PointCh(2_532_745, 1_152_388);
        PointCh point5 = new PointCh(2_532_755, 1_152_398);

        List<PointCh> pointsList = new ArrayList<>(List.of(point0, point1, point2, point3, point4, point5));

        assertEquals(pointsList, DIAGONAL_PATH.points());
    }

    @Test
    void pointAtWorks() {
        PointCh pointOnHorizontalRoute = new PointCh(2_532_700 + 9200 + 800, 1152348.0);
        PointCh pointOnVerticalRoute = new PointCh(2_532_705, 1_152_300 + 9200 + 800);
        PointCh pointOnDiagonalRoute = new PointCh(2_532_755, 1_152_398);
        assertEquals(pointOnHorizontalRoute, HORIZONTAL_PATH.pointAt(10000));
        assertEquals(pointOnVerticalRoute, VERTICAL_PATH.pointAt(10000));
        assertEquals(pointOnDiagonalRoute, DIAGONAL_PATH.pointAt(10000));
    }



    @Test
    void elevationAtWorks() {
        assertEquals(800, HORIZONTAL_PATH.elevationAt(10000));
        assertEquals(800, VERTICAL_PATH.elevationAt(10000));
        assertEquals(9943.431457505076, DIAGONAL_PATH.elevationAt(10000));
    }

    @Test
    void nodeClosestToWorks() {
        assertEquals(3, HORIZONTAL_PATH.nodeClosestTo(10000));
        assertEquals(3, VERTICAL_PATH.nodeClosestTo(10000));
        assertEquals(5, DIAGONAL_PATH.nodeClosestTo(10000));
    }




    @Test
    void pointClosestToWorksOnKnownValues() {
        PointCh point3 = new PointCh(2_532_700 + 9200 + 2, 1_152_348);
        PointCh referencePoint3 = new PointCh(2_532_700 + 9200 + 2, 1_152_348);
        RoutePoint routePoint3 = new RoutePoint(point3, 9200 + 2, 0);

        RoutePoint actualRoutePoint = HORIZONTAL_PATH.pointClosestTo(referencePoint3);

        assertEquals(routePoint3.point(), actualRoutePoint.point());
        assertEquals(routePoint3.position(), actualRoutePoint.position());
        assertEquals(routePoint3.distanceToReference(), actualRoutePoint.distanceToReference(), DELTA);



        PointCh point5 = new PointCh(2_532_700 + 13100, 1_152_348);
        PointCh referencePoint5 = new PointCh(2_532_700 + 13100+ 2, 1_152_348);
        RoutePoint routePoint5 = new RoutePoint(point5, 13100, 2);

        RoutePoint actualRoutePoint1 = HORIZONTAL_PATH.pointClosestTo(referencePoint5);


        assertEquals(routePoint5.point(), actualRoutePoint1.point());
        assertEquals(routePoint5.position(), actualRoutePoint1.position());
        assertEquals(routePoint5.distanceToReference(), actualRoutePoint1.distanceToReference(), DELTA);




        PointCh point3V = new PointCh(2_532_705, 1_152_300 + 9200 + 2);
        PointCh referencePoint3V = new PointCh(2_532_705, 1_152_300 + 9200 + 2);
        RoutePoint routePoint3V = new RoutePoint(point3V, 9200 + 2, 0);

        RoutePoint actualRoutePoint3V = VERTICAL_PATH.pointClosestTo(referencePoint3V);

        assertEquals(routePoint3V.point(), actualRoutePoint3V.point());
        assertEquals(routePoint3V.position(), actualRoutePoint3V.position());
        assertEquals(routePoint3V.distanceToReference(), actualRoutePoint3V.distanceToReference(), DELTA);
    }

    @Test
    void testZebra() {
        PointCh pointZ = new PointCh(2485000, 1075000);
        PointCh referencePointZ = new PointCh(2485000, 1075030);
        RoutePoint routePointZ = new RoutePoint(pointZ, 0, 30);

        RoutePoint actualRoutePointZ = ZEBRA_PATH.pointClosestTo(referencePointZ);

        assertEquals(routePointZ.point(), actualRoutePointZ.point());
        assertEquals(routePointZ.position(), actualRoutePointZ.position());
        assertEquals(routePointZ.distanceToReference(), actualRoutePointZ.distanceToReference(), DELTA);
    }
}