package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.DoubleUnaryOperator;
import java.util.stream.Stream;

import static ch.epfl.test.TestRandomizer.RANDOM_ITERATIONS;
import static ch.epfl.test.TestRandomizer.newRandom;
import static org.junit.jupiter.api.Assertions.*;

class MultiRouteTest {
    private static final int ORIGIN_N = 1_200_000;
    private static final int ORIGIN_E = 2_600_000;
    private static final double EDGE_LENGTH = 100.25;

    // Sides of triangle used for "sawtooth" edges (shape: /\/\/\…)
    private static final double TOOTH_EW = 1023;
    private static final double TOOTH_NS = 64;
    private static final double TOOTH_LENGTH = 1025;
    private static final double TOOTH_ELEVATION_GAIN = 100d;
    private static final double TOOTH_SLOPE = TOOTH_ELEVATION_GAIN / TOOTH_LENGTH;


    @Test
    void indexOfSegmentAt() {
        double[] longueur = new double[]{1000, 1000, 1000, 1000, 1000, 1000};
        List<Edge> lesEdges = RouteBuilder.straightRouteFlat(longueur);
        List<Route> lesRoutes = new ArrayList<>();
        lesRoutes.add(new SingleRoute(List.of(lesEdges.get(0))));
        lesRoutes.add(new SingleRoute(List.of(lesEdges.get(1))));
        lesRoutes.add(new SingleRoute(List.of(lesEdges.get(2))));
        lesRoutes.add(new SingleRoute(List.of(lesEdges.get(3))));
        lesRoutes.add(new SingleRoute(List.of(lesEdges.get(4))));
        lesRoutes.add(new SingleRoute(List.of(lesEdges.get(5))));
        //List<List<Route>>
        MultiRoute finale = new MultiRoute(lesRoutes);
        for (int i = 0; i < 6; i++) {
            assertEquals(i, finale.indexOfSegmentAt(i * 1000 + 500));
        }

    }

    @Test
    void length() {
        double[] longueur = new double[]{1000, 1000, 1000, 1000, 1000, 1000};
        List<Edge> lesEdges = RouteBuilder.straightRouteFlat(longueur);
        List<Route> lesRoutes = new ArrayList<>();
        lesRoutes.add(new SingleRoute(List.of(lesEdges.get(0))));
        lesRoutes.add(new SingleRoute(List.of(lesEdges.get(1))));
        lesRoutes.add(new SingleRoute(List.of(lesEdges.get(2))));
        lesRoutes.add(new SingleRoute(List.of(lesEdges.get(3))));
        lesRoutes.add(new SingleRoute(List.of(lesEdges.get(4))));
        lesRoutes.add(new SingleRoute(List.of(lesEdges.get(5))));
        MultiRoute finale = new MultiRoute(lesRoutes);
        assertEquals(6000, finale.length());
    }

    @Test
    void edgesWorksOnSingleRoutesAndMultiRoutes() {
        double[] longueur = new double[]{1000, 1000, 1000, 1000, 1000, 1000};
        List<Edge> lesEdges = RouteBuilder.straightRouteFlat(longueur);
        List<Route> lesRoutes = new ArrayList<>();
        List<Route> lesRoutesBinks = new ArrayList<>();
        lesRoutes.add(new SingleRoute(List.of(lesEdges.get(0))));
        lesRoutes.add(new SingleRoute(List.of(lesEdges.get(1))));
        lesRoutes.add(new SingleRoute(List.of(lesEdges.get(2))));
        MultiRoute yolohahah = new MultiRoute(lesRoutes);
        lesRoutes.add(new SingleRoute(List.of(lesEdges.get(4))));
        lesRoutes.add(new SingleRoute(List.of(lesEdges.get(5))));
        lesRoutes.add(new SingleRoute(List.of(lesEdges.get(3))));
        lesRoutesBinks.add(new SingleRoute(List.of(lesEdges.get(4))));
        lesRoutesBinks.add(new SingleRoute(List.of(lesEdges.get(5))));
        lesRoutesBinks.add(new SingleRoute(List.of(lesEdges.get(3))));
        MultiRoute jspgros = new MultiRoute(lesRoutesBinks);
        MultiRoute plusieursRoutes = new MultiRoute(Arrays.asList(new Route[]{yolohahah, jspgros}));
        MultiRoute finale = new MultiRoute(lesRoutes);
        List<Edge> plusieursEdges = plusieursRoutes.edges();
        List<Edge> ouaisouais = finale.edges();
        for (int i = 0; i < ouaisouais.size(); i++) {
            assertTrue(ouaisouais.contains(new Edge(i, i + 1, new PointCh(SwissBounds.MIN_E + i * 1000, SwissBounds.MIN_N), new
                    PointCh(SwissBounds.MIN_E + (i + 1) * 1000, SwissBounds.MIN_N), 1000, Functions.constant(1))));
            assertTrue(plusieursEdges.contains(new Edge(i, i + 1, new PointCh(SwissBounds.MIN_E + i * 1000, SwissBounds.MIN_N), new
                    PointCh(SwissBounds.MIN_E + (i + 1) * 1000, SwissBounds.MIN_N), 1000, Functions.constant(1))));
        }
    }


    @Test
    void MultiRouteThrowsOnEmptyList() {
        assertThrows(IllegalArgumentException.class, () -> {
            MultiRoute slt = new MultiRoute(List.of());
        });
    }
    @Test
    void MultiRouteIsImmutable(){
        double[] longueur = new double[]{1000, 1000, 1000, 1000, 1000, 1000};
        List<Edge> lesEdges = RouteBuilder.straightRouteFlat(longueur);
        List<Route> lesRoutes = new ArrayList<>();
        List<Route> lesRoutesBinks = new ArrayList<>();
        lesRoutes.add(new SingleRoute(List.of(lesEdges.get(0))));
        lesRoutes.add(new SingleRoute(List.of(lesEdges.get(1))));
        lesRoutes.add(new SingleRoute(List.of(lesEdges.get(2))));
        MultiRoute yolohahah = new MultiRoute(lesRoutes);
        lesRoutes.add(new SingleRoute(List.of(lesEdges.get(3))));
        MultiRoute salut = new MultiRoute(lesRoutes);
        assertNotEquals(salut.edges(), yolohahah.edges());
    }

    @Test
    void points() {
        double[] longueur = new double[]{1000, 1000, 1000, 1000, 1000, 1000};
        List<Edge> lesEdges = RouteBuilder.straightRouteFlat(longueur);
        List<Route> lesRoutes = new ArrayList<>();
        lesRoutes.add(new SingleRoute(List.of(lesEdges.get(0))));
        lesRoutes.add(new SingleRoute(List.of(lesEdges.get(1))));
        lesRoutes.add(new SingleRoute(List.of(lesEdges.get(2))));
        lesRoutes.add(new SingleRoute(List.of(lesEdges.get(3))));
        lesRoutes.add(new SingleRoute(List.of(lesEdges.get(4))));
        lesRoutes.add(new SingleRoute(List.of(lesEdges.get(5))));
        MultiRoute finale = new MultiRoute(lesRoutes);
        for (int i = 0; i < 7; i++) {
            assertTrue(finale.points().contains(new PointCh(SwissBounds.MIN_E + 1000 * i, SwissBounds.MIN_N)));
        }
        assertEquals(7, finale.points().size());
    }

    @Test
    void pointAt() {
        double[] longueur = new double[]{1000, 1000, 1000, 1000, 1000, 1000};
        List<Edge> lesEdges = RouteBuilder.straightRouteFlat(longueur);
        List<Route> lesRoutes = new ArrayList<>();
        lesRoutes.add(new SingleRoute(List.of(lesEdges.get(0))));
        lesRoutes.add(new SingleRoute(List.of(lesEdges.get(1))));
        lesRoutes.add(new SingleRoute(List.of(lesEdges.get(2))));
        lesRoutes.add(new SingleRoute(List.of(lesEdges.get(3))));
        lesRoutes.add(new SingleRoute(List.of(lesEdges.get(4))));
        lesRoutes.add(new SingleRoute(List.of(lesEdges.get(5))));
        MultiRoute finale = new MultiRoute(lesRoutes);
        assertEquals(1f, finale.elevationAt(1000));
        assertEquals(new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), finale.pointAt(0));
        assertEquals(new PointCh(SwissBounds.MIN_E + 6000, SwissBounds.MIN_N), finale.pointAt(finale.length()));
    }

    @Test
    void elevationAt() {
        double[] longueur = new double[]{1000, 1000, 1000, 1000, 1000, 1000};
        List<Edge> lesEdges = RouteBuilder.straightRouteFlat(longueur);
        List<Route> lesRoutes = new ArrayList<>();
        lesRoutes.add(new SingleRoute(List.of(lesEdges.get(0))));
        lesRoutes.add(new SingleRoute(List.of(lesEdges.get(1))));
        lesRoutes.add(new SingleRoute(List.of(lesEdges.get(2))));
        lesRoutes.add(new SingleRoute(List.of(lesEdges.get(3))));
        lesRoutes.add(new SingleRoute(List.of(lesEdges.get(4))));
        lesRoutes.add(new SingleRoute(List.of(lesEdges.get(5))));
        MultiRoute finale = new MultiRoute(lesRoutes);
        assertEquals(1f, finale.elevationAt(1000));
    }

    @Test
    void nodeClosestTo() throws IOException {

        Graph routeGraph = Graph.loadFrom(Path.of("lausanne"));
        Edge edge1 = Edge.of(routeGraph, 0, 0, routeGraph.edgeTargetNodeId(0));
        Edge edge2 = Edge.of(routeGraph, 2, 1, routeGraph.edgeTargetNodeId(2));
        List<Edge> edges = List.of(edge1);
        Route coucou = new SingleRoute(edges);
        Route salut = new SingleRoute(List.of(edge2));
        List<Route> listeRoute = List.of(coucou, salut);
        MultiRoute multiRoute = new MultiRoute(listeRoute);
        assertEquals(0, multiRoute.nodeClosestTo(10));
        assertEquals(1, multiRoute.nodeClosestTo(60));
        assertEquals(2, multiRoute.nodeClosestTo(150));

        double[] longueur = new double[]{1000, 1000, 1000, 1000, 1000, 1000};
        List<Edge> lesEdges = RouteBuilder.straightRouteFlat(longueur);
        List<Route> lesRoutes = new ArrayList<>();
        lesRoutes.add(new SingleRoute(List.of(lesEdges.get(0))));
        lesRoutes.add(new SingleRoute(List.of(lesEdges.get(1))));
        lesRoutes.add(new SingleRoute(List.of(lesEdges.get(2))));
        lesRoutes.add(new SingleRoute(List.of(lesEdges.get(3))));
        lesRoutes.add(new SingleRoute(List.of(lesEdges.get(4))));
        lesRoutes.add(new SingleRoute(List.of(lesEdges.get(5))));
        MultiRoute finale = new MultiRoute(lesRoutes);
        assertEquals(0, finale.nodeClosestTo(499));
        assertEquals(0, finale.nodeClosestTo(500));
        assertEquals(1, finale.nodeClosestTo(501));
        assertEquals(6, finale.nodeClosestTo(finale.length()));
    }

    @Test
    void pointClosestToWorksOnRoad() throws IOException {
        Graph routeGraph = Graph.loadFrom(Path.of("lausanne"));
        Edge edge1 = Edge.of(routeGraph, 0, 0, routeGraph.edgeTargetNodeId(0));
        Edge edge2 = Edge.of(routeGraph, 2, 1, routeGraph.edgeTargetNodeId(2));
        List<Edge> edges = List.of(edge1);
        Route coucou = new SingleRoute(edges);
        Route salut = new SingleRoute(List.of(edge2));
        List<Route> listeRoute = List.of(coucou, salut);
        MultiRoute multiRoute = new MultiRoute(listeRoute);
        System.out.println(multiRoute.pointClosestTo(new PointCh(2549213, 1166183.5625)));
        System.out.println(multiRoute.pointClosestTo(new PointCh(2549278.75, 1166253)));
        double[] longueur = new double[]{1000, 1000, 1000, 1000, 1000, 1000};
        List<Edge> lesEdges = RouteBuilder.straightRouteFlat(longueur);
        List<Route> lesRoutes = new ArrayList<>();
        lesRoutes.add(new SingleRoute(List.of(lesEdges.get(0))));
        lesRoutes.add(new SingleRoute(List.of(lesEdges.get(1))));
        lesRoutes.add(new SingleRoute(List.of(lesEdges.get(2))));
        lesRoutes.add(new SingleRoute(List.of(lesEdges.get(3))));
        lesRoutes.add(new SingleRoute(List.of(lesEdges.get(4))));
        lesRoutes.add(new SingleRoute(List.of(lesEdges.get(5))));
        MultiRoute finale = new MultiRoute(lesRoutes);
        assertEquals(new RoutePoint(new PointCh(SwissBounds.MIN_E + 501, SwissBounds.MIN_N), 501, 0), finale.pointClosestTo(new PointCh(SwissBounds.MIN_E + 501, SwissBounds.MIN_N)));
    }

    private static List<Edge> verticalEdges(int edgesCount) {
        var edges = new ArrayList<Edge>(edgesCount);
        for (int i = 0; i < edgesCount; i += 1) {
            var p1 = new PointCh(ORIGIN_E, ORIGIN_N + i * EDGE_LENGTH);
            var p2 = new PointCh(ORIGIN_E, ORIGIN_N + (i + 1) * EDGE_LENGTH);
            edges.add(new Edge(i, i + 1, p1, p2, EDGE_LENGTH, x -> Double.NaN));
        }
        return Collections.unmodifiableList(edges);
    }

    private static List<Edge> sawToothEdges(int edgesCount) {
        var edges = new ArrayList<Edge>(edgesCount);
        for (int i = 0; i < edgesCount; i += 1) {
            var p1 = sawToothPoint(i);
            var p2 = sawToothPoint(i + 1);
            var startingElevation = i * TOOTH_ELEVATION_GAIN;
            edges.add(new Edge(i, i + 1, p1, p2, TOOTH_LENGTH, x -> startingElevation + x * TOOTH_SLOPE));
        }
        return Collections.unmodifiableList(edges);
    }

    private static PointCh sawToothPoint(int i) {
        return new PointCh(
                ORIGIN_E + TOOTH_EW * i,
                ORIGIN_N + ((i & 1) == 0 ? 0 : TOOTH_NS));
    }

    @Test
    void multiRoutePointClosestToWorksWithSawtoothPoints() {
        var edgesCount = 8;
        List<Edge> sawTooth = (sawToothEdges(8));
        var route = new MultiRoute(List.of(new SingleRoute(List.of(sawTooth.get(0), sawTooth.get(1))),
                new SingleRoute(List.of(sawTooth.get(2), sawTooth.get(3))), new SingleRoute(List.of(sawTooth.get(4), sawTooth.get(5))), new SingleRoute(List.of(sawTooth.get(6), sawTooth.get(7)))));

        // Points above the sawtooth
        for (int i = 1; i <= 8; i += 2) {
            var p = sawToothPoint(i);
            var dN = i * 500;
            var pAbove = new PointCh(p.e(), p.n() + dN);
            var pct = route.pointClosestTo(pAbove);
            assertEquals(p, pct.point());
            assertEquals(i * TOOTH_LENGTH, pct.position());
            assertEquals(dN, pct.distanceToReference());
        }

        // Points below the sawtooth
        for (int i = 0; i <= edgesCount; i += 2) {
            var p = sawToothPoint(i);
            var dN = i * 500;
            var pBelow = new PointCh(p.e(), p.n() - dN);
            var pct = route.pointClosestTo(pBelow);
            assertEquals(p, pct.point());
            assertEquals(i * TOOTH_LENGTH, pct.position());
            assertEquals(dN, pct.distanceToReference());
        }

        // Points close to the n/8
        var dE = TOOTH_NS / 16d;
        var dN = TOOTH_EW / 16d;
        for (int i = 0; i < 4; i += 1) {
            var upwardEdge = (i & 1) == 0;
            for (double p = 0.125; p <= 0.875; p += 0.125) {
                var pointE = ORIGIN_E + (i + p) * TOOTH_EW;
                var pointN = ORIGIN_N + TOOTH_NS * (upwardEdge ? p : (1 - p));
                var point = new PointCh(pointE, pointN);
                var position = (i + p) * TOOTH_LENGTH;
                var reference = new PointCh(
                        pointE + dE,
                        pointN + (upwardEdge ? -dN : dN));
                var pct = route.pointClosestTo(reference);
                assertEquals(point, pct.point());
                assertEquals(position, pct.position());
                assertEquals(Math.hypot(dE, dN), pct.distanceToReference());
            }
        }
    }

    @Test
    void multiRoutePointClosestToWorksWithPointsOnRoute() {
        var rng = newRandom();
        var list = verticalEdges(20);
        List<Route> routes = new ArrayList<>();
        for (int i = 0; i < 20; i += 2) {
            routes.add(new SingleRoute(List.of(list.get(i), list.get(i + 1))));
        }
        var route = new MultiRoute(routes);
        for (int i = 0; i < RANDOM_ITERATIONS; i += 1) {
            var pos = rng.nextDouble(0, route.length());
            var pt = route.pointAt(pos);
            var pct = route.pointClosestTo(pt);
            assertEquals(pt.e(), pct.point().e(), 1e-4);
            assertEquals(pt.n(), pct.point().n(), 1e-4);
            //   assertEquals(pos, pct.position(), 1e-4);
            assertEquals(0, pct.distanceToReference(), 1e-4);
        }
    }

    public static final List<PointCh> ALL_POINTS = allPoints();
    public static final List<Edge> ALL_EDGES = allEdges();
    public static final MultiRoute MULTI_ROUTE = multiRoute();

    public static List<PointCh> allPoints() {
        PointCh point0 = new PointCh(2_550_000, 1_152_300);
        PointCh point1 = new PointCh(2_550_500, 1_152_300);
        PointCh point2 = new PointCh(2_551_000, 1_152_300);
        PointCh point3 = new PointCh(2_551_500, 1_152_300);
        PointCh point4 = new PointCh(2_552_000, 1_152_300);
        PointCh point5 = new PointCh(2_552_500, 1_152_300);
        PointCh point6 = new PointCh(2_553_000, 1_152_300);
        PointCh point7 = new PointCh(2_553_500, 1_152_300);
        PointCh point8 = new PointCh(2_554_000, 1_152_300);
        PointCh point9 = new PointCh(2_554_500, 1_152_300);
        PointCh point10 = new PointCh(2_555_000, 1_152_300);
        PointCh point11 = new PointCh(2_555_500, 1_152_300);
        PointCh point12 = new PointCh(2_556_000, 1_152_300);

        List<PointCh> allPoints = new ArrayList<>(List.of(point0, point1, point2, point3, point4, point5, point6,
                point7, point8, point9, point10, point11, point12));

        return allPoints;
    }

    public static List<Edge> allEdges() {

        Edge edge0 = new Edge(0, 1, ALL_POINTS.get(0), ALL_POINTS.get(1), 500, DoubleUnaryOperator.identity());
        Edge edge1 = new Edge(1, 2, ALL_POINTS.get(1), ALL_POINTS.get(2), 500, DoubleUnaryOperator.identity());
        Edge edge2 = new Edge(2, 3, ALL_POINTS.get(2), ALL_POINTS.get(3), 500, DoubleUnaryOperator.identity());
        Edge edge3 = new Edge(3, 4, ALL_POINTS.get(3), ALL_POINTS.get(4), 500, DoubleUnaryOperator.identity());
        Edge edge4 = new Edge(4, 5, ALL_POINTS.get(4), ALL_POINTS.get(5), 500, DoubleUnaryOperator.identity());
        Edge edge5 = new Edge(5, 6, ALL_POINTS.get(5), ALL_POINTS.get(6), 500, DoubleUnaryOperator.identity());
        Edge edge6 = new Edge(6, 7, ALL_POINTS.get(6), ALL_POINTS.get(7), 500, DoubleUnaryOperator.identity());
        Edge edge7 = new Edge(7, 8, ALL_POINTS.get(7), ALL_POINTS.get(8), 500, DoubleUnaryOperator.identity());
        Edge edge8 = new Edge(8, 9, ALL_POINTS.get(8), ALL_POINTS.get(9), 500, DoubleUnaryOperator.identity());
        Edge edge9 = new Edge(9, 10, ALL_POINTS.get(9), ALL_POINTS.get(10), 500, DoubleUnaryOperator.identity());
        Edge edge10 = new Edge(10, 11, ALL_POINTS.get(10), ALL_POINTS.get(11), 500, DoubleUnaryOperator.identity());
        Edge edge11 = new Edge(11, 12, ALL_POINTS.get(11), ALL_POINTS.get(12), 500, DoubleUnaryOperator.identity());

        List<Edge> edges0 = new ArrayList<>(List.of(edge0, edge1));
        List<Edge> edges1 = new ArrayList<>(List.of(edge2, edge3));
        List<Edge> edges2 = new ArrayList<>(List.of(edge4, edge5));
        List<Edge> edges3 = new ArrayList<>(List.of(edge6, edge7));
        List<Edge> edges4 = new ArrayList<>(List.of(edge8, edge9));
        List<Edge> edges5 = new ArrayList<>(List.of(edge10, edge11));

        List<Edge> allEdges = new ArrayList<>();
        Stream.of(edges0, edges1, edges2, edges3, edges4, edges5).forEach(allEdges::addAll);

        return allEdges;
    }

    public static MultiRoute multiRoute() {

        Route singleRoute0 = new SingleRoute(ALL_EDGES.subList(0, 2));
        Route singleRoute1 = new SingleRoute(ALL_EDGES.subList(2, 4));
        Route singleRoute2 = new SingleRoute(ALL_EDGES.subList(4, 6));
        Route singleRoute3 = new SingleRoute(ALL_EDGES.subList(6, 8));
        Route singleRoute4 = new SingleRoute(ALL_EDGES.subList(8, 10));
        Route singleRoute5 = new SingleRoute(ALL_EDGES.subList(10, 12));

        List<Route> segment0 = new ArrayList<>(List.of(singleRoute0, singleRoute1, singleRoute2));
        List<Route> segment1 = new ArrayList<>(List.of(singleRoute3, singleRoute4, singleRoute5));

        MultiRoute multiRoute0 = new MultiRoute(segment0);
        MultiRoute multiRoute1 = new MultiRoute(segment1);
        List<Route> segments = List.of(multiRoute0, multiRoute1);

        // Stream.of(segment0, segment1).forEach(segments::addAll);

        return new MultiRoute(segments);
    }

    @Test
    void indexOfSegmentAtWorksOnKnownValues() {
        assertEquals(0, MULTI_ROUTE.indexOfSegmentAt(-400));
        assertEquals(0, MULTI_ROUTE.indexOfSegmentAt(100));
        assertEquals(3, MULTI_ROUTE.indexOfSegmentAt(4000));
        assertEquals(4, MULTI_ROUTE.indexOfSegmentAt(4100));
        assertEquals(5, MULTI_ROUTE.indexOfSegmentAt(5500));
        assertEquals(5, MULTI_ROUTE.indexOfSegmentAt(6000));
        assertEquals(5, MULTI_ROUTE.indexOfSegmentAt(12000));
    }

    @Test
    void edgesWorksForAllEdges() {
        assertEquals(ALL_EDGES, MULTI_ROUTE.edges());
    }

    @Test
    void pointsWorksForAllPoints() {
        assertEquals(ALL_POINTS, MULTI_ROUTE.points());
    }

    @Test
    void pointAtWorksOnKnownValues() {
        PointCh pointOnRoute5 = new PointCh(2_555_500, 1_152_300);
        PointCh pointOnRoute0 = new PointCh(2_550_000, 1_152_300);
        PointCh pointOnRoute5end = new PointCh(2_556_000, 1_152_300);
        assertEquals(pointOnRoute5, MULTI_ROUTE.pointAt(5500));
        assertEquals(pointOnRoute5end, MULTI_ROUTE.pointAt(6000));
        assertEquals(pointOnRoute5end, MULTI_ROUTE.pointAt(7000));
        assertEquals(pointOnRoute0, MULTI_ROUTE.pointAt(0));
        assertEquals(pointOnRoute0, MULTI_ROUTE.pointAt(-2));
    }

    @Test
    void elevationAtWorksOnKnownValues() {
        assertEquals(0, MULTI_ROUTE.elevationAt(5500));
        assertEquals(500, MULTI_ROUTE.elevationAt(6000));
        assertEquals(0, MULTI_ROUTE.elevationAt(0));
    }

    @Test
    void nodeClosestToWorksOnKnownValues() {
        assertEquals(6, MULTI_ROUTE.nodeClosestTo(3000));
        assertEquals(6, MULTI_ROUTE.nodeClosestTo(2990));
        assertEquals(11, MULTI_ROUTE.nodeClosestTo(5575));
        assertEquals(11, MULTI_ROUTE.nodeClosestTo(5500));
        assertEquals(11, MULTI_ROUTE.nodeClosestTo(5750));
        assertEquals(12, MULTI_ROUTE.nodeClosestTo(5751));
        assertEquals(12, MULTI_ROUTE.nodeClosestTo(7700));
        assertEquals(0, MULTI_ROUTE.nodeClosestTo(-1));
    }

    @Test
    void pointClosestToWorksOnKnownPoints() {

        PointCh point6 = new PointCh(2_553_000 + 10, 1_152_300);
        PointCh point6Reference = new PointCh(2_553_000 + 10, 1_152_300 + 50);
        RoutePoint routePoint6 = new RoutePoint(point6, 3000 + 10, 50);

        PointCh point11 = new PointCh(2_555_500, 1_152_300);
        PointCh point11Reference = new PointCh(2_555_500, 1_152_300 + 50);
        RoutePoint routePoint11 = new RoutePoint(point11, 5500, 50);

        assertEquals(routePoint6, MULTI_ROUTE.pointClosestTo(point6Reference));
        assertEquals(routePoint11, MULTI_ROUTE.pointClosestTo(point11Reference));
    }
}