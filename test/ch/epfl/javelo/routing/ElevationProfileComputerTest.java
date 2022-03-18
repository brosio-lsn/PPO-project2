package ch.epfl.javelo.routing;

import ch.epfl.javelo.data.Graph;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ElevationProfileComputerTest {

    @Test
    void elevationProfile() throws IOException {
        Graph routeGraph = Graph.loadFrom(Path.of("lausanne"));
        Edge edge1 = Edge.of(routeGraph, 0, 0, routeGraph.edgeTargetNodeId(0));
        Edge edge2 = Edge.of(routeGraph, 1, 1, routeGraph.edgeTargetNodeId(1));
        List<Edge> edges = new ArrayList<>();
        edges.add(edge1); //edges.add(edge2);
        Route coucou = new SingleRoute(edges);
        System.out.println();
        System.out.println(routeGraph.edgeElevationGain(0));
        System.out.println(ElevationProfileComputer.elevationProfile(coucou, 0.5));

    }


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
        float [] samples = new float[]{Float.NaN};
        assertArrayEquals(new float[] {0}, ElevationProfileComputer.fillTheHoles(samples));
    }
}