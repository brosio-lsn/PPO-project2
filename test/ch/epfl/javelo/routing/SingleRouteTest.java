package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.Math2;
import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.DoubleUnaryOperator;

import static org.junit.jupiter.api.Assertions.*;

class SingleRouteTest {

    @Test
    void indexOfSegmentAt() {
        assertThrows(IllegalArgumentException.class, () ->{
            SingleRoute single = new SingleRoute(new ArrayList<>());
                });
    }

    @Test
    void length() {
        float[] yeet = {30f, 31f, 30.5f, 34f, 29f, 30f, 35f, 29.5f, 33f, 31f,60f};
        PointCh fromPoint= new PointCh(SwissBounds.MIN_E+500, SwissBounds.MIN_N+200);
        PointCh toPoint= new PointCh(SwissBounds.MIN_E+510, SwissBounds.MIN_N+205);
        double length = Math2.norm((toPoint.n()-fromPoint.n()),(toPoint.e()-fromPoint.e()));
        DoubleUnaryOperator yo = Functions.sampled(yeet,length);
        Edge edge1 = new Edge(0,50,fromPoint,toPoint, length, yo);

        float[] yeet2 = {28f, 30f, 30.5f, 34f, 26f, 30f, 37f, 29.5f, 32f, 31f,80f};
        PointCh fromPoint2= new PointCh(SwissBounds.MIN_E+510, SwissBounds.MIN_N+205);
        PointCh toPoint2= new PointCh(SwissBounds.MIN_E+522, SwissBounds.MIN_N+211);
        double length2 = Math2.norm((toPoint2.n()-fromPoint2.n()),(toPoint2.e()-fromPoint2.e()));
        DoubleUnaryOperator yo2 = Functions.sampled(yeet2,length2);
        Edge edge2 = new Edge(51,62,fromPoint2,toPoint2, length2, yo2);

        ArrayList<Edge> list = new ArrayList<>();
        list.add(edge1);
        list.add(edge2);
        SingleRoute single = new SingleRoute(list);
        assertEquals(length+length2, single.length(), 10E-2);
    }

    @Test
    void edges() {
        float[] yeet = {30f, 31f, 30.5f, 34f, 29f, 30f, 35f, 29.5f, 33f, 31f,60f};
        PointCh fromPoint= new PointCh(SwissBounds.MIN_E+500, SwissBounds.MIN_N+200);
        PointCh toPoint= new PointCh(SwissBounds.MIN_E+510, SwissBounds.MIN_N+205);
        double length = Math2.norm((toPoint.n()-fromPoint.n()),(toPoint.e()-fromPoint.e()));
        DoubleUnaryOperator yo = Functions.sampled(yeet,length);
        Edge edge1 = new Edge(0,50,fromPoint,toPoint, length, yo);

        float[] yeet2 = {28f, 30f, 30.5f, 34f, 26f, 30f, 37f, 29.5f, 32f, 31f,80f};
        PointCh fromPoint2= new PointCh(SwissBounds.MIN_E+510, SwissBounds.MIN_N+205);
        PointCh toPoint2= new PointCh(SwissBounds.MIN_E+522, SwissBounds.MIN_N+211);
        double length2 = Math2.norm((toPoint2.n()-fromPoint2.n()),(toPoint2.e()-fromPoint2.e()));
        DoubleUnaryOperator yo2 = Functions.sampled(yeet2,length2);
        Edge edge2 = new Edge(51,62,fromPoint2,toPoint2, length2, yo2);

        ArrayList<Edge> list = new ArrayList<>();
        list.add(edge1);
        list.add(edge2);
        SingleRoute single = new SingleRoute(list);
        assertEquals(list, single.edges());
        assertThrows(UnsupportedOperationException.class, () ->{
            single.edges().add(edge1);
            int b=0;
        });
    }

    @Test
    void points() {
        float[] yeet = {30f, 31f, 30.5f, 34f, 29f, 30f, 35f, 29.5f, 33f, 31f,60f};
        PointCh fromPoint= new PointCh(SwissBounds.MIN_E+500, SwissBounds.MIN_N+200);
        PointCh toPoint= new PointCh(SwissBounds.MIN_E+510, SwissBounds.MIN_N+205);
        double length = Math2.norm((toPoint.n()-fromPoint.n()),(toPoint.e()-fromPoint.e()));
        DoubleUnaryOperator yo = Functions.sampled(yeet,length);
        Edge edge1 = new Edge(0,50,fromPoint,toPoint, length, yo);

        float[] yeet2 = {28f, 30f, 30.5f, 34f, 26f, 30f, 37f, 29.5f, 32f, 31f,80f};
        PointCh fromPoint2= new PointCh(SwissBounds.MIN_E+510, SwissBounds.MIN_N+205);
        PointCh toPoint2= new PointCh(SwissBounds.MIN_E+522, SwissBounds.MIN_N+211);
        double length2 = Math2.norm((toPoint2.n()-fromPoint2.n()),(toPoint2.e()-fromPoint2.e()));
        DoubleUnaryOperator yo2 = Functions.sampled(yeet2,length2);
        Edge edge2 = new Edge(51,62,fromPoint2,toPoint2, length2, yo2);

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
        float[] yeet = {30f, 31f, 30.5f, 34f, 29f, 30f, 35f, 29.5f, 33f, 31f,60f};
        PointCh fromPoint= new PointCh(SwissBounds.MIN_E+500, SwissBounds.MIN_N+200);
        PointCh toPoint= new PointCh(SwissBounds.MIN_E+510, SwissBounds.MIN_N+205);
        double length = Math2.norm((toPoint.n()-fromPoint.n()),(toPoint.e()-fromPoint.e()));
        DoubleUnaryOperator yo = Functions.sampled(yeet,length);
        Edge edge1 = new Edge(0,50,fromPoint,toPoint, length, yo);

        float[] yeet2 = {28f, 30f, 30.5f, 34f, 26f, 30f, 37f, 29.5f, 32f, 31f,80f};
        PointCh fromPoint2= new PointCh(SwissBounds.MIN_E+510, SwissBounds.MIN_N+205);
        PointCh toPoint2= new PointCh(SwissBounds.MIN_E+522, SwissBounds.MIN_N+211);
        double length2 = Math2.norm((toPoint2.n()-fromPoint2.n()),(toPoint2.e()-fromPoint2.e()));
        DoubleUnaryOperator yo2 = Functions.sampled(yeet2,length2);
        Edge edge2 = new Edge(51,62,fromPoint2,toPoint2, length2, yo2);


        ArrayList<Edge> list = new ArrayList<>();
        list.add(edge1);
        list.add(edge2);
        SingleRoute single = new SingleRoute(list);

        assertEquals(edge1.pointAt(5.5), single.pointAt(5.5));
        assertEquals(edge2.pointAt(2), single.pointAt(length+2));
        assertEquals(edge2.pointAt(0), single.pointAt(length));
        assertEquals(edge2.pointAt(length2), single.pointAt(length+length2));
        assertEquals(edge1.pointAt(0), single.pointAt(0));
        assertEquals(edge1.pointAt(length), single.pointAt(length));
        //assertEquals(fromPoint, single.pointAt(-10));
        single.pointAt(length+length2+1);
        assertEquals(edge2.pointAt(length2), single.pointAt(length*40));
        assertEquals(edge1.pointAt(0), single.pointAt(-10));
    }

    @Test
    void nodeClosestTo() {
        float[] yeet = {30f, 31f, 30.5f, 34f, 29f, 30f, 35f, 29.5f, 33f, 31f,60f};
        PointCh fromPoint= new PointCh(SwissBounds.MIN_E+500, SwissBounds.MIN_N+200);
        PointCh toPoint= new PointCh(SwissBounds.MIN_E+510, SwissBounds.MIN_N+205);
        double length = Math2.norm((toPoint.n()-fromPoint.n()),(toPoint.e()-fromPoint.e()));
        DoubleUnaryOperator yo = Functions.sampled(yeet,length);
        Edge edge1 = new Edge(0,50,fromPoint,toPoint, length, yo);

        float[] yeet2 = {28f, 30f, 30.5f, 34f, 26f, 30f, 37f, 29.5f, 32f, 31f,80f};
        PointCh fromPoint2= new PointCh(SwissBounds.MIN_E+510, SwissBounds.MIN_N+205);
        PointCh toPoint2= new PointCh(SwissBounds.MIN_E+522, SwissBounds.MIN_N+211);
        double length2 = Math2.norm((toPoint2.n()-fromPoint2.n()),(toPoint2.e()-fromPoint2.e()));
        DoubleUnaryOperator yo2 = Functions.sampled(yeet2,length2);
        Edge edge2 = new Edge(50,62,fromPoint2,toPoint2, length2, yo2);


        ArrayList<Edge> list = new ArrayList<>();
        list.add(edge1);
        list.add(edge2);
        SingleRoute single = new SingleRoute(list);

        assertEquals(0, single.nodeClosestTo(1.5));
        assertEquals(50, single.nodeClosestTo(length));
        assertEquals(50, single.nodeClosestTo(length-1));
        assertEquals(50, single.nodeClosestTo(length+1));
        assertEquals(62, single.nodeClosestTo(length+length2-1));
        assertEquals(0, single.nodeClosestTo(-10));
        assertEquals(62, single.nodeClosestTo(5000));
        assertEquals(0, single.nodeClosestTo(length/2));
        single.nodeClosestTo(length+length2);
        assertEquals(62, single.nodeClosestTo(length+length2));
        assertEquals(0, single.nodeClosestTo(-100));
        //single.nodeClosestTo(length+length2);
        assertEquals(62, single.nodeClosestTo(length+length2+6));
    }

    @Test
    void pointClosestTo() {
        float[] yeet = {30f, 31f, 30.5f, 34f, 29f, 30f, 35f, 29.5f, 33f, 31f,60f};
        PointCh fromPoint= new PointCh(SwissBounds.MIN_E+500, SwissBounds.MIN_N+200);
        PointCh toPoint= new PointCh(SwissBounds.MIN_E+510, SwissBounds.MIN_N+205);
        double length = Math2.norm((toPoint.n()-fromPoint.n()),(toPoint.e()-fromPoint.e()));
        DoubleUnaryOperator yo = Functions.sampled(yeet,length);
        Edge edge1 = new Edge(0,50,fromPoint,toPoint, length, yo);

        float[] yeet2 = {28f, 30f, 30.5f, 34f, 26f, 30f, 37f, 29.5f, 32f, 31f,80f};
        PointCh fromPoint2= new PointCh(SwissBounds.MIN_E+510, SwissBounds.MIN_N+205);
        PointCh toPoint2= new PointCh(SwissBounds.MIN_E+522, SwissBounds.MIN_N+211);
        double length2 = Math2.norm((toPoint2.n()-fromPoint2.n()),(toPoint2.e()-fromPoint2.e()));
        DoubleUnaryOperator yo2 = Functions.sampled(yeet2,length2);
        Edge edge2 = new Edge(50,62,fromPoint2,toPoint2, length2, yo2);


        ArrayList<Edge> list = new ArrayList<>();
        list.add(edge1);
        list.add(edge2);
        SingleRoute single = new SingleRoute(list);
        single.pointClosestTo(new PointCh(SwissBounds.MIN_E+400, SwissBounds.MIN_N+190));
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
        System.out.println(edge2.pointAt( edge2.positionClosestTo(new PointCh(SwissBounds.MIN_E+503.6, SwissBounds.MIN_N+206.9))).distanceTo(new PointCh(SwissBounds.MIN_E+503.6, SwissBounds.MIN_N+206.9)));
        assertEquals(edge2.pointAt( edge2.positionClosestTo(new PointCh(SwissBounds.MIN_E+503.6, SwissBounds.MIN_N+206.9))), single.pointClosestTo(new PointCh(SwissBounds.MIN_E+503.6, SwissBounds.MIN_N+206.9)));



    }

    @Test
    void elevationAt() {

        float[] yeet = {30f, 31f, 30.5f, 34f, 29f, 30f, 35f, 29.5f, 33f, 31f,60f};
        PointCh fromPoint= new PointCh(SwissBounds.MIN_E+500, SwissBounds.MIN_N+200);
        PointCh toPoint= new PointCh(SwissBounds.MIN_E+510, SwissBounds.MIN_N+205);
        double length = Math2.norm((toPoint.n()-fromPoint.n()),(toPoint.e()-fromPoint.e()));
        DoubleUnaryOperator yo = Functions.sampled(yeet,length);
        Edge edge1 = new Edge(0,50,fromPoint,toPoint, length, yo);

        float[] yeet2 = {28f, 30f, 30.5f, 34f, 26f, 30f, 37f, 29.5f, 32f, 31f,80f};
        PointCh fromPoint2= new PointCh(SwissBounds.MIN_E+510, SwissBounds.MIN_N+205);
        PointCh toPoint2= new PointCh(SwissBounds.MIN_E+522, SwissBounds.MIN_N+211);
        double length2 = Math2.norm((toPoint2.n()-fromPoint2.n()),(toPoint2.e()-fromPoint2.e()));
        DoubleUnaryOperator yo2 = Functions.sampled(yeet2,length2);
        Edge edge2 = new Edge(50,62,fromPoint2,toPoint2, length2, yo2);


        ArrayList<Edge> list = new ArrayList<>();
        list.add(edge1);
        list.add(edge2);
        SingleRoute single = new SingleRoute(list);

        assertEquals(edge1.elevationAt(1), single.elevationAt(1));
        assertEquals(edge1.elevationAt(length-1), single.elevationAt(length-1));
        assertEquals(edge2.elevationAt(1), single.elevationAt(length+1));
        assertEquals(edge2.elevationAt(0), single.elevationAt(length));
        assertEquals(edge2.elevationAt(length2-1), single.elevationAt(length+length2-1));
        assertEquals(edge1.elevationAt(-1), single.elevationAt(-10));
        assertEquals(edge2.elevationAt(60000), single.elevationAt(5000));
        assertEquals(edge1.elevationAt(length/2), single.elevationAt(length/2));
        assertEquals(30f, single.elevationAt(0));
        single.elevationAt(length+length2);
        single.elevationAt(length+length2);
        assertEquals(80f, single.elevationAt(length+length2));
        assertEquals(80f, single.elevationAt(length+length2+6));
        assertEquals(30f, single.elevationAt(-100));
    }

    private static final double DELTA = 1e-2;
    private static final SingleRoute HORIZONTAL_PATH = HorizontalPath(); // Constant north coordinate
    private static final SingleRoute VERTICAL_PATH = VerticalPath(); // Constant east coordinate
    private static final SingleRoute DIAGONAL_PATH = DiagonalPath(); //
    private static final SingleRoute ZEBRA_PATH = ZebraPath(); // Zebra path

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
        float[] yeet = {30f, 31f, 30.5f, 34f, 29f, 30f, 35f, 29.5f, 33f, 31f,60f};
        DoubleUnaryOperator yo = Functions.sampled(yeet, Math.sqrt(200));
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
        System.out.println(HorizontalPath().edges().get(HorizontalPath().edges().size()-1).elevationAt(1701));
        System.out.println(HorizontalPath().length());
        System.out.println(HorizontalPath().edges().get(HorizontalPath().edges().size()-1).length());
        assertEquals(60, DIAGONAL_PATH.elevationAt(HorizontalPath().length()*100));
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



        //TODO OUT OF BOUND PROBLEM
        PointCh point5 = new PointCh(2_532_700 + 13100, 1_152_348);
        PointCh referencePoint5 = new PointCh(2_532_700 + 13100 + 2, 1_152_348);
        RoutePoint routePoint5 = new RoutePoint(point5, 13100, 0);

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