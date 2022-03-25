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
import java.util.List;
import java.util.Random;
import java.util.function.DoubleUnaryOperator;

import static org.junit.jupiter.api.Assertions.*;

class ElevationProfileComputerTest {
    @Test
    void elevationProfileThrowsWhenIllegalStep() throws IOException {
        Graph routeGraph = Graph.loadFrom(Path.of("lausanne"));
        Edge edge1 = Edge.of(routeGraph, 0, 0, routeGraph.edgeTargetNodeId(0));
        List<Edge> edges = new ArrayList<>();
        edges.add(edge1);
        Route coucou = new SingleRoute(edges);
        assertThrows(IllegalArgumentException.class, () -> {
            ElevationProfileComputer.elevationProfile(new SingleRoute(new ArrayList<Edge>()), 5);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            ElevationProfileComputer.elevationProfile(coucou, 0.0);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            ElevationProfileComputer.elevationProfile(coucou, -1.0);
        });

    }
    @Test
    void elevationProfile() throws IOException {
        Graph routeGraph = Graph.loadFrom(Path.of("lausanne"));
        Edge edge1 = Edge.of(routeGraph, 0, 0, routeGraph.edgeTargetNodeId(0));
        Edge edge2 = Edge.of(routeGraph, 1, 1, routeGraph.edgeTargetNodeId(1));
        List<Edge> edges = new ArrayList<>();
        edges.add(edge1);
        edges.add(edge2);
        Route coucou = new SingleRoute(edges);
        ElevationProfile wesh = ElevationProfileComputer.elevationProfile(coucou, 1.0);
        int nbOfProfiles = (int)Math.ceil(coucou.length()) + 1;
        double stepLengthOfCoucou = nbOfProfiles/coucou.length();
        System.out.println("Statistiques de la route coucou, qui va du noeud 0 à 2, (edgeID 0 et 1)");
        System.out.println("Gain d'élévation de la route 'coucou' :" + routeGraph.edgeElevationGain(0));
        System.out.println("Descente totale de la route 'coucou' :" + wesh.totalDescent());
        System.out.println("Montée totale de la route 'coucou' :" + wesh.totalAscent());
        System.out.println("Altitude MAX de la route 'coucou' : " + wesh.maxElevation());
        System.out.println("Altitude MIN de la route 'coucou': " + wesh.minElevation());
        System.out.println("échantillons de la route 'coucou' (MaxPas = 1.0):");
        for (int i = 0; i <= nbOfProfiles; i++) {
            System.out.println(i + " : " + wesh.elevationAt(i*stepLengthOfCoucou));
        }
        double routeLength = 20; // 2meters
      /*  Edge edge = new Edge(0, 10, new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N),
                new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N + 20), 20, Functions.sampled(
                new float[]{1f, 1f, 1f, 3f, Float.NaN, 7f, Float.NaN, Float.NaN, Float.NaN, 2f, Float.NaN},
                routeLength));
        List<Edge> salut = new ArrayList<>();
        salut.add(edge);
        float[] elevationProfileComputer1Excepcted = new float[]{1f, 1f, 1f, 3f, 5f, 7f, 5.75f, 4.5f, 3.25f, 2f, 2f};//todo : boucle for imbriquee
        Route MDRR = new SingleRoute(salut);

       */
        List<float[]> lesTableaux = new ArrayList<>();
        float[][] lesEdges = new float[5][];
        double[] longueurs = new double[5];
        lesEdges[0] = new float[]{2f, 3F, 3f};
        longueurs[0] = 4;
        lesTableaux.add(lesEdges[0]);
        lesEdges[1] = new float[]{Float.NaN, Float.NaN, Float.NaN, Float.NaN};
        longueurs[1] = 6;
        lesTableaux.add(lesEdges[1]);
        lesEdges[2] = new float[]{3f, 4f, 2f};
        longueurs[2] = 4;
        lesTableaux.add(lesEdges[2]);
        lesEdges[3] = new float[]{Float.NaN, Float.NaN, Float.NaN};
        longueurs[3] =4;
        lesTableaux.add(lesEdges[3]);
        lesEdges[4] = new float[]{0f, 1f, 2f, 3f, 4f};
        longueurs[4] = 8;
        lesTableaux.add(lesEdges[4]);
        int nbOfProfilesOfLaRoute = (int) Math.ceil(26.0/2) + 1;
        double stepLengthOfLaRoute = 26.0 / nbOfProfiles;
        Route laRoute = routeCreator(lesTableaux, longueurs);
        ElevationProfile elevationRoute = ElevationProfileComputer.elevationProfile(laRoute, 1.0);
        System.out.println("============");
        System.out.println("Statistiques de la Route laRoute tkt ça marche");
        System.out.println("Descente totale de la route 'laRoute' :" + elevationRoute.totalDescent());
        System.out.println("Montée totale de la route 'laRoute' :" + elevationRoute.totalAscent());
        System.out.println("Altitude MAX de la route 'laRoute' : " + elevationRoute.maxElevation());
        System.out.println("Altitude MIN de la route 'laRoute': " + elevationRoute.minElevation());
        System.out.println("échantillons de la route 'laRoute' (MaxPas = 1.0):" );
        for (int i = 0; i < nbOfProfilesOfLaRoute; i++) {
            System.out.println(i + " : " + elevationRoute.elevationAt(i * stepLengthOfLaRoute));
        }

        //    ElevationProfile expected = new ElevationProfile(20, elevationProfileComputer1Excepcted);
          //  ElevationProfile actual = ElevationProfileComputer.elevationProfile(MDRR, 0.01);
            //elevationTests(expected, actual);
            ElevationProfile weakActual = ElevationProfileComputer.elevationProfile(routeCreator(lesTableaux, longueurs), 0.001);
            ElevationProfile bigBossExpected = new ElevationProfile(26.0, new float[]{2f, 3f, 3f, 3f, 3f, 3f, 4f, 2f, 1f, 0f, 1f, 2f, 3f, 4f});
            elevationTests(bigBossExpected, weakActual);
        }

        void elevationTests (ElevationProfile uno, ElevationProfile dos){
            System.out.println("-> Tests avec les valeurs attendues (delta = 1e-2)");
            System.out.print("maxElevation...");
            assertEquals(uno.maxElevation(), dos.maxElevation(), 0.01);
            System.out.println("passed!");
            System.out.print("minElevation...");
            assertEquals(uno.minElevation(), dos.minElevation(), 0.01);
            System.out.println("passed!");
            System.out.print("totalAscent...");
            assertEquals(uno.totalAscent(), dos.totalAscent(), 0.01);
            System.out.println("passed!");
            System.out.print("totalDescent...");
            assertEquals(uno.totalDescent(), dos.totalDescent(), 0.01);
            System.out.println("passed!");
            System.out.print("ElevationAt...");
            Random coucou = new Random(2002);
            for (int i = 0; i < 100; i++) {
                double salut = coucou.nextDouble(uno.length());
                System.out.println("position : " + salut);
                assertEquals(uno.elevationAt(salut), dos.elevationAt(salut), 0.1);
            }
            System.out.print("passed!");
        }
    private Route routeCreator(List<float[]> slt, double[] longueurs) {
        double sum =0;
        List<Edge> listePourLesRoutes = new ArrayList<>();
        for (double longueur : longueurs) {
            sum += longueur;
        }
        for (int i = 0; i < slt.size(); i++) {
            listePourLesRoutes.add(new Edge(0, 10, new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N),
                    new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N + 20), longueurs[i], Functions.sampled(slt.get(i), longueurs[i])));
        }
        return new SingleRoute(listePourLesRoutes);
    }
    //méthode auxiliaire
    @Test
    void fillTheHolesWorksWithNormalTab() {
        float[] samples = new float [] {Float.NaN, 23f, Float.NaN, 25f, Float.NaN};
        float [] samples2 = new float [] {Float.NaN, 20f, Float.NaN, Float.NaN, 23f, Float.NaN};
        samples = ElevationProfileComputer.fillTheHoles(samples);
        samples2 = ElevationProfileComputer.fillTheHoles(samples2);
        System.out.println(Arrays.toString(samples2));
        float[] expectedResult = new float[] {23f, 23f, 24f, 25f, 25f};
        assertArrayEquals(expectedResult, samples);
    }
    @Test
    void fillTheHolesWorksWithNAN() {
        float [] samples = new float[]{Float.NaN, Float.NaN};
        assertArrayEquals(new float[] {0, 0}, ElevationProfileComputer.fillTheHoles(samples));
    }
    @Test
    void elevationProfileDoesThrowErrorOnInvalidArg() throws IOException {
        assertThrows(IllegalArgumentException.class, () -> {
            ElevationProfileComputer.elevationProfile(new SingleRoute(new ArrayList<Edge>()), 5);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            ArrayList<Edge> edge1 = new ArrayList<Edge>();
            Path filePath = Path.of("lausanne");
            Graph e = Graph.loadFrom(filePath);
            edge1.add(Edge.of(e, 0, 1, 2));
            ElevationProfileComputer.elevationProfile(new SingleRoute(edge1), 0);
        });

    }

    @Test
    void elevationProfileDoesNotThrowErrorOnValidArg() {
        assertDoesNotThrow(() -> {
            ArrayList<Edge> edge1 = new ArrayList<Edge>();
            Path filePath = Path.of("lausanne");
            Graph e = Graph.loadFrom(filePath);
            edge1.add(Edge.of(e, 0, 1, 2));
            edge1.add(Edge.of(e, 1, 1, 2));
            edge1.add(Edge.of(e, 2, 1, 2));

            ElevationProfileComputer.elevationProfile(new SingleRoute(edge1), 10);
        });
    }

    @Test
    void elevationProfileExceptionTest(){
        int fromNodeId = 0;
        int toNodeId = 10;
        PointCh fromPoint = new PointCh(2485000, 1075000);
        PointCh toPointCh = new PointCh(2485100, 1075100);
        double length = 100;
        float[] samples = {300, 310, 305, 320, 300, 290, 305, 300, 310, 300};
        DoubleUnaryOperator a = Functions.sampled(samples, 100);
        Edge edge = new Edge(fromNodeId, toNodeId, fromPoint, toPointCh, length, a);
        List<Edge> edges = new ArrayList<>();
        edges.add(edge);
        SingleRoute route = new SingleRoute(edges);

        assertThrows(IllegalArgumentException.class, () -> {
            ElevationProfile elevationProfile1 = ElevationProfileComputer.elevationProfile(route, 0);
        } );

        assertThrows(IllegalArgumentException.class, () -> {
            ElevationProfile elevationProfile1 = ElevationProfileComputer.elevationProfile(route, -1);
        } );
    }

    @Test
    void OnlyNaNTest(){
        int fromNodeId = 0;
        int toNodeId = 10;
        PointCh fromPoint = new PointCh(2485000, 1075000);
        PointCh toPointCh = new PointCh(2485100, 1075100);
        double length = 100;
        float[] samples = {Float.NaN, Float.NaN, Float.NaN, Float.NaN, Float.NaN, Float.NaN, Float.NaN, Float.NaN, Float.NaN, Float.NaN};
        DoubleUnaryOperator a = Functions.sampled(samples, 100);
        Edge edge = new Edge(fromNodeId, toNodeId, fromPoint, toPointCh, length, a);
        List<Edge> edges = new ArrayList<>();
        edges.add(edge);
        SingleRoute route = new SingleRoute(edges);

        ElevationProfile NaNProfile = ElevationProfileComputer.elevationProfile(route, 10);
        assertEquals(0, NaNProfile.minElevation());
        assertEquals(0, NaNProfile.maxElevation());
        assertEquals(0, NaNProfile.totalAscent());
        assertEquals(0, NaNProfile.totalDescent());
    }

    @Test
    void BeginningNaNTest(){
        int fromNodeId = 0;
        int toNodeId = 10;
        PointCh fromPoint = new PointCh(2485000, 1075000);
        PointCh toPointCh = new PointCh(2485100, 1075100);
        double length = 100;

        float[] samples = {Float.NaN, Float.NaN, Float.NaN, 500, 502, 505, 510, 500, 520, 510, 500};
        DoubleUnaryOperator a = Functions.sampled(samples, 100);
        Edge edge = new Edge(fromNodeId, toNodeId, fromPoint, toPointCh, length, a);
        List<Edge> edges = new ArrayList<>();
        edges.add(edge);
        SingleRoute route = new SingleRoute(edges);

        ElevationProfile profile = ElevationProfileComputer.elevationProfile(route, 10.0);
        assertEquals(500, profile.minElevation(), 0.00001);
    }

    @Test
    void EndNaNTest(){
        int fromNodeId = 0;
        int toNodeId = 10;
        PointCh fromPoint = new PointCh(2485000, 1075000);
        PointCh toPointCh = new PointCh(2485100, 1075100);
        double length = 100;
        float[] samples = {500, 502, 505, 510, 500, 520, 510, Float.NaN, Float.NaN, Float.NaN};
        DoubleUnaryOperator a = Functions.sampled(samples, 100);
        Edge edge = new Edge(fromNodeId, toNodeId, fromPoint, toPointCh, length, a);
        List<Edge> edges = new ArrayList<>();
        edges.add(edge);
        SingleRoute route = new SingleRoute(edges);

        ElevationProfile profile = ElevationProfileComputer.elevationProfile(route, 10);
        assertEquals(500, profile.minElevation());
    }

    @Test
    void MiddleNaNTest(){
        int fromNodeId = 0;
        int toNodeId = 10;
        PointCh fromPoint = new PointCh(2485000, 1075000);
        PointCh toPointCh = new PointCh(2485100, 1075100);
        double length = 100;
        //float[] samples = {500, 500, 500, 500, 500, Float.NaN, 500, 500,500,500 };
        float[] samples = {500, 500, 500, 500, 500, Float.NaN, Float.NaN, Float.NaN, 500, 500};
        DoubleUnaryOperator a = Functions.sampled(samples, 100);
        Edge edge = new Edge(fromNodeId, toNodeId, fromPoint, toPointCh, length, a);
        List<Edge> edges = new ArrayList<>();
        edges.add(edge);
        SingleRoute route = new SingleRoute(edges);

        ElevationProfile profile = ElevationProfileComputer.elevationProfile(route, 10);
        assertEquals(500, profile.minElevation());
    }
}