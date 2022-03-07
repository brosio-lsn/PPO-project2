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
        assertEquals(0.353664, WebMercator.y(Math.toRadians(46.5218)));
    }

    @Test
    void lon() {
        assertEquals(6.579, (WebMercator.lon(Math.toRadians(0.518275214444))));
    }

    @Test
    void lat() {
        assertEquals(46.521, (WebMercator.lat(Math.toRadians(0.353664894749))));
    }
}