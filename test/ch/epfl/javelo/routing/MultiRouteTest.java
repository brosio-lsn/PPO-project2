package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MultiRouteTest {

    static void coucou(){

    }
    @Test
    void indexOfSegmentAt() {
        List<float[]> slt = new ArrayList<>();
        slt.add(new float[]{1f, 1f});
        double [] longueur = new double[] {1000};
        SingleRoute one = (SingleRoute)RouteBuilder.routeCreator(slt, longueur);
        SingleRoute two = (SingleRoute)RouteBuilder.routeCreator(slt, longueur);
        SingleRoute three = (SingleRoute)RouteBuilder.routeCreator(slt, longueur);
        List<Route> lesRoutes = new ArrayList<>();
        lesRoutes.add(one); lesRoutes.add(two); lesRoutes.add(three);
        lesRoutes.addAll(lesRoutes);
        MultiRoute finale = new MultiRoute(lesRoutes);
        assertEquals(5, finale.indexOfSegmentAt(5500));

    }

    @Test
    void length() {
        List<float[]> slt = new ArrayList<>();
        slt.add(new float[]{1f, 1f});
        double [] longueur = new double[] {1000};
        SingleRoute one = (SingleRoute)RouteBuilder.routeCreator(slt, longueur);
        SingleRoute two = (SingleRoute)RouteBuilder.routeCreator(slt, longueur);
        SingleRoute three = (SingleRoute)RouteBuilder.routeCreator(slt, longueur);
        List<Route> lesRoutes = new ArrayList<>();
        lesRoutes.add(one); lesRoutes.add(two); lesRoutes.add(three);
        lesRoutes.addAll(lesRoutes);
        MultiRoute finale = new MultiRoute(lesRoutes);
        assertEquals(6000, finale.length());
    }

    @Test
    void edges() {
        List<float[]> slt = new ArrayList<>();
        slt.add(new float[]{1f, 1f});
        double [] longueur = new double[] {1000};
        SingleRoute one = (SingleRoute)RouteBuilder.routeCreator(slt, longueur);
        SingleRoute two = (SingleRoute)RouteBuilder.routeCreator(slt, longueur);
        SingleRoute three = (SingleRoute)RouteBuilder.routeCreator(slt, longueur);
        List<Route> lesRoutes = new ArrayList<>();
        lesRoutes.add(one); lesRoutes.add(two); lesRoutes.add(three);
        lesRoutes.addAll(lesRoutes);
        MultiRoute finale = new MultiRoute(lesRoutes);
        List<Edge> ouaisouais = finale.edges();
      //  assertTrue(finale.edges().contains(new Edge(0, 10, new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N),
       //         new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N + 20), 1000, Functions.sampled(new float[] {1f, 1f}, 1000))));
    }

    @Test
    void points() {
        List<float[]> slt = new ArrayList<>();
        slt.add(new float[]{1f, 1f});
        double [] longueur = new double[] {1000};
        SingleRoute one = (SingleRoute)RouteBuilder.routeCreator(slt, longueur);
        SingleRoute two = (SingleRoute)RouteBuilder.routeCreator(slt, longueur);
        SingleRoute three = (SingleRoute)RouteBuilder.routeCreator(slt, longueur);
        List<Route> lesRoutes = new ArrayList<>();
        lesRoutes.add(one); lesRoutes.add(two); lesRoutes.add(three);
        lesRoutes.addAll(lesRoutes);
        MultiRoute finale = new MultiRoute(lesRoutes);
        assertTrue(finale.points().contains(new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N))
                && finale.points().contains(new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N + 20)));
        assertEquals(2, finale.points().size());
    }

    @Test
    void pointAt() {
        List<float[]> slt = new ArrayList<>();
        slt.add(new float[]{1f, 1f});
        double [] longueur = new double[] {1000};
        SingleRoute one = (SingleRoute)RouteBuilder.routeCreator(slt, longueur);
        SingleRoute two = (SingleRoute)RouteBuilder.routeCreator(slt, longueur);
        SingleRoute three = (SingleRoute)RouteBuilder.routeCreator(slt, longueur);
        List<Route> lesRoutes = new ArrayList<>();
        lesRoutes.add(one); lesRoutes.add(two); lesRoutes.add(three);
        lesRoutes.addAll(lesRoutes);
        MultiRoute finale = new MultiRoute(lesRoutes);
        assertEquals(new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), finale.pointAt(0));
        assertEquals(new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N+20), finale.pointAt(finale.length()));
    }

    @Test
    void elevationAt() {
        List<float[]> slt = new ArrayList<>();
        slt.add(new float[]{1f, 1f});
        double [] longueur = new double[] {1000};
        SingleRoute one = (SingleRoute)RouteBuilder.routeCreator(slt, longueur);
        SingleRoute two = (SingleRoute)RouteBuilder.routeCreator(slt, longueur);
        SingleRoute three = (SingleRoute)RouteBuilder.routeCreator(slt, longueur);
        List<Route> lesRoutes = new ArrayList<>();
        lesRoutes.add(one); lesRoutes.add(two); lesRoutes.add(three);
        lesRoutes.addAll(lesRoutes);
        MultiRoute finale = new MultiRoute(lesRoutes);
        assertEquals(1f, finale.elevationAt(1000));
    }

    @Test
    void nodeClosestTo() {
    }

    @Test
    void pointClosestTo() {
    }
}