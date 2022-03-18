package ch.epfl.javelo.routing;

import ch.epfl.javelo.Bits;
import ch.epfl.javelo.Math2;
import org.junit.jupiter.api.Test;

import java.util.DoubleSummaryStatistics;

import static org.junit.jupiter.api.Assertions.*;

class ElevationProfileTest {
    @Test
    void constructorDoesThrowErrorOnInvalidArg() {
        assertThrows(IllegalArgumentException.class, () -> {ElevationProfile a = new ElevationProfile(0,new float[5]);});
        assertThrows(IllegalArgumentException.class, () -> {ElevationProfile a = new ElevationProfile(1,new float[2]);});
        assertThrows(IllegalArgumentException.class, () -> {ElevationProfile a = new ElevationProfile(-5,new float[1]);});

    }

    @Test
    void constructorDoesNotThrowErrorOnValidArg() {
        assertDoesNotThrow(() -> {ElevationProfile a = new ElevationProfile(5,new float[5]);});
    }

    @Test
    public void lengthWorksOnSampleTests(){
        ElevationProfile a = new ElevationProfile(5,new float[5]);
        assertEquals(5  ,a.length());

    }

    @Test
    public void minAndMaxElevationWorkOnSampleTests(){
        DoubleSummaryStatistics s1 = new DoubleSummaryStatistics();
        s1.accept(5.25);
        s1.accept(-3.125);
        s1.accept(0);
        assertEquals(-3.125, s1.getMin());
        assertEquals(5.25, s1.getMax());

        DoubleSummaryStatistics s2 = new DoubleSummaryStatistics();
        s2.accept(13);
        s2.accept(-1111111111);
        s2.accept(111111);
        assertEquals(-1111111111, s2.getMin());
        assertEquals(111111, s2.getMax());
    }


    @Test
    public void totalAscentAndDescentWorkOnSampleTests(){

        ElevationProfile a2 = new ElevationProfile(5,new float[]{120,200,12,0,1,3,2000});
        assertEquals(2080, a2.totalAscent());
        assertEquals(200, a2.totalDescent());

        ElevationProfile a3 = new ElevationProfile(5,new float[]{384.075f, 384.6875f, 384.5625f, 384.5f, 384.4375f,
                384.375f, 384.3125f, 384.25f, 384.125f, 384.0625f, 383.0f});
        //assertEquals(0.6125, a3.totalAscent());
        assertEquals(1.6875, a3.totalDescent());

        ElevationProfile a1 = new ElevationProfile(5,new float[]{606.75f,606.8125f,607.0f,607.125f,607.1875f,
                607.25f,607.375f,607.4375f,607.5625f,607.625f,607.6875f,607.8125f});

        assertEquals(1.0625, a1.totalAscent());
        assertEquals(0, a1.totalDescent());

        ElevationProfile a4 = new ElevationProfile(5,new float[]{607.8125f,607.6875f,607.625f,607.5625f,607.4375f,
                607.25f,607.375f,607.4375f});

        assertEquals(0.1875, a4.totalAscent());
        assertEquals(0.5625, a4.totalDescent());


    }



    @Test
    public void elevationAtWorksOnSampleTests() {

        ElevationProfile a1 = new ElevationProfile(5, new float[]{384.075f, 384.6875f, 384.5625f, 384.5f, 384.4375f,
                384.375f, 384.3125f});

        assertEquals(Math2.interpolate(384.5625f, 384.5f, 2.4 - 2), a1.elevationAt(2));


        ElevationProfile a2 = new ElevationProfile(3, new float[]{607.9375f, 608.0f, 608.0f, 608.125f, 608.1875f, 608.125f, 608.1875f, 608.25f, 608.25f, 608.25f, 608.375f,
                608.4375f, 608.4375f, 608.5f, 608.5625f});

        double x02 = 2 * (14.0 / 3.0);
        int x012 = (int) x02;

        assertEquals(Math2.interpolate(608.25f, 608.375f, x02 - x012), a2.elevationAt(2));

        ElevationProfile a3 = new ElevationProfile(3, new float[]{607.9375f, 608.0f, 608.0f, 608.125f, 608.1875f, 608.125f, 608.1875f, 608.25f, 608.25f, 608.25f, 608.375f,
                608.4375f, 608.4375f, 608.5f, 608.5625f});

        assertEquals(608.5625f, a3.elevationAt(10));

        assertEquals(607.9375f, a3.elevationAt(-2));
    }



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

    float[] expectedSamples1 = new float[]{
            384.0625f, 384.125f, 384.25f, 384.3125f, 384.375f,
            384.4375f, 384.5f, 384.5625f, 384.6875f, 384.75f
    };

    float[] expectedSamples3 = new float[]{
            384.0625f, 384.125f, 384.25f, 384.1f, 384.012f
    };

    float[] expectedSamples2 = new float[]{
            384.0625f
    };

    @Test
    void elevationProfileTest() {
        assertThrows(IllegalArgumentException.class, () -> {
            new ElevationProfile(0, expectedSamples1);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new ElevationProfile(-6, expectedSamples2);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new ElevationProfile(2, expectedSamples2);
        });
    }

    @Test
    void minElevationTest() {
        DoubleSummaryStatistics s = new DoubleSummaryStatistics();
        ElevationProfile profile = new ElevationProfile(2, expectedSamples1);
        for (int i = 0; i < expectedSamples1.length; i++) {
            s.accept(expectedSamples1[i]);
        }
        assertEquals(s.getMin(), profile.minElevation());
    }

    @Test
    void maxElevationTest() {
        DoubleSummaryStatistics s = new DoubleSummaryStatistics();
        ElevationProfile profile = new ElevationProfile(2, expectedSamples1);
        for (int i = 0; i < expectedSamples1.length; i++) {
            s.accept(expectedSamples1[i]);
        }
        assertEquals(s.getMax(), profile.maxElevation());
    }

    @Test
    void totalAscentTest() {
        ElevationProfile profile = new ElevationProfile(3, expectedSamples3);
        assertEquals(0.1875, profile.totalAscent());
    }

    @Test
    void totalDescentTest() {
        ElevationProfile profile = new ElevationProfile(3, expectedSamples3);
        assertEquals(0.238, profile.totalDescent(), 1e-3);
    }

}