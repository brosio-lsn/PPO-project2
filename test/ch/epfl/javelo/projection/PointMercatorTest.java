package ch.epfl.javelo.projection;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PointMercatorTest {

    @Test
    void construcror(){
        assertThrows(IllegalArgumentException.class, () -> {
            new PointMercator(-1,3);
        });
    }

    @Test
    void getX() {
        PointMercator p =new PointMercator(0,0.5);
        p.getX();
        assertEquals("0:1", p.getX());
    }

    @Test
    void of() {
    }

    @Test
    void ofPointCh() {
    }

    @Test
    void xAtZoomLevel() {
    }

    @Test
    void yAtZoomLevel() {
    }

    @Test
    void lon() {
    }

    @Test
    void lat() {
    }

    @Test
    void toPointCh() {
    }

    @Test
    void x() {
    }

    @Test
    void y() {
    }
}