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
        assertEquals(0.35366489, WebMercator.y(Math.toRadians(46.5218976)));
    }
}