package ch.epfl.javelo.routing;

import ch.epfl.javelo.Bits;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ElevationProfileTest {

    @Test
    void length() {
        float[] yeet = {30f, 31f, 30.5f, 34f, 29f, 30f, 35f, 29.5f, 33f, 31f};
        ElevationProfile p = new ElevationProfile(9, yeet);
        assertEquals(9, p.length());

    }

    @Test
    void minElevation() {
        float[] yeet = {30f, 31f, 30.5f, 34f, 29f, 30f, 35f, 29.5f, 33f, 31f};
        ElevationProfile p = new ElevationProfile(9, yeet);
        assertEquals(29, p.minElevation());
    }

    @Test
    void maxElevation() {
        float[] yeet = {30f, 31f, 30.5f, 34f, 29f, 30f, 35f, 29.5f, 33f, 31f};
        ElevationProfile p = new ElevationProfile(9, yeet);
        assertEquals(35, p.maxElevation());
    }

    @Test
    void totalAscent() {
        float[] yeet = {30f, 31f, 30.5f, 34f, 29f, 30f, 35f, 29.5f, 33f, 31f};
        ElevationProfile p = new ElevationProfile(9, yeet);
        assertEquals(14, p.totalAscent());
    }

    @Test
    void totalDescent() {
            float[] yeet = {30f, 31f, 30.5f, 34f, 29f, 30f, 35f, 29.5f, 33f, 31f};
            ElevationProfile p = new ElevationProfile(9, yeet);
            assertEquals(13, p.totalDescent());
    }

    @Test
    void elevationAt() {
        float[] yeet = {30f, 31f, 30.5f, 34f, 29f, 30f, 35f, 29.5f, 33f, 31f,60f};
        ElevationProfile p = new ElevationProfile(10, yeet);
        assertEquals(45.5, p.elevationAt(9.5));
        assertEquals(60, p.elevationAt(92));
        assertEquals(30, p.elevationAt(-10));
        assertThrows(IllegalArgumentException.class, () -> {
            float[] lol = {30f};
            ElevationProfile oh = new ElevationProfile(0, yeet);
        });
    }
}