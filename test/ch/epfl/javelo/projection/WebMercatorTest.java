package ch.epfl.javelo.projection;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WebMercatorTest {

    @Test
    void x() {
        assertEquals(0.51827, WebMercator.x(Math.toRadians(6.5790772)));
    }

    @Test
    void y() {
        assertEquals(0.5, WebMercator.y(Math.toRadians(0)));
    }

    @Test
    void lon() {
        assertEquals(0.5, Math.toDegrees(WebMercator.lon(0.518275214444)));
    }

    @Test
    void lat() {
        assertEquals(0.5, Math.toDegrees(WebMercator.lat(0.353664894749)));
    }
}