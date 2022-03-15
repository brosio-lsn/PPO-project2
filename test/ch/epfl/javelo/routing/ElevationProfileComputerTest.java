package ch.epfl.javelo.routing;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class ElevationProfileComputerTest {

    @Test
    void elevationProfile() throws IOException {
        Route coucou = new RouteSampleTest();
        System.out.println(ElevationProfileComputer.elevationProfile(coucou, 1.0));
    }
    @Test
    void fillTheHolesWorksWithNormalTab() {
        float[] samples = new float [] {Float.NaN, 23f, Float.NaN, 25f, Float.NaN};
        samples = ElevationProfileComputer.fillTheHoles(samples);
        float[] expectedResult = new float[] {23f, 23f, 24f, 25f, 25f};
        assertArrayEquals(expectedResult, samples);
    }
    @Test
    void fillTheHolesWorksWithNAN() {
        float [] samples = new float[]{Float.NaN};
        assertArrayEquals(new float[] {0}, ElevationProfileComputer.fillTheHoles(samples));
    }
}