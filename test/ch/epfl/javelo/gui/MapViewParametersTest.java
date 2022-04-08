package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointWebMercator;
import javafx.geometry.Point2D;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MapViewParametersTest {

    @Test
    void topLeft() {
        MapViewParameters map = new MapViewParameters(10, 15,16);
        assertEquals(new Point2D(15,16), map.topLeft() );
    }

    @Test
    void withMinXY() {
        MapViewParameters map = new MapViewParameters(10, 15,16);
        assertEquals(new Point2D(15,16), map.topLeft() );
        MapViewParameters map2 = new MapViewParameters(10, 20,42);
        assertEquals(map2, map.withMinXY(20,42));
    }

    @Test
    void pointAt() {
        MapViewParameters map = new MapViewParameters(10, 15,16);
        PointWebMercator p = PointWebMercator.of(10,10,5);
        assertEquals(p, map.pointAt(-5,-11));
    }

    @Test
    void viewX() {
        MapViewParameters map = new MapViewParameters(10, 15,16);
        assertEquals(0, map.viewX(map.pointAt(0,0)));
        assertEquals(-5, map.viewX(map.pointAt(-5,-11)));
    }

    @Test
    void viewY() {
        MapViewParameters map = new MapViewParameters(10, 15,16);
        assertEquals(0, map.viewY(map.pointAt(0,0)));
        assertEquals(-11, map.viewY(map.pointAt(-5,-11)));
    }

}