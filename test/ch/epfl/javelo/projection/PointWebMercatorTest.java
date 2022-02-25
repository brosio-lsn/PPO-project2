package ch.epfl.javelo.projection;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PointWebMercatorTest {

    @Test
    void construcror(){
        assertThrows(IllegalArgumentException.class, () -> {
            new PointWebMercator(-1,3);
        });
    }

    @Test
    void getX() {
        PointWebMercator p =new PointWebMercator(0,1);
        p.getX();
        //System.out.println(p.getX());
        assertEquals("0.0:1.0", p.getX());
    }

    @Test
    void of() {
        PointWebMercator.of(19,69561722,47468099);
        assertEquals("ff", PointWebMercator.of(19,69561722,47468099).getX());
    }

    @Test
    void ofPointCh() {
        double x = Ch1903.e(Math.toRadians(6.5790772),Math.toRadians(46.5218976));
        PointWebMercator p = PointWebMercator.ofPointCh(new PointCh(Ch1903.e(Math.toRadians(6.5790772),Math.toRadians(46.5218976)), Ch1903.n(Math.toRadians(6.5790772),Math.toRadians(46.5218976))));
        assertEquals("ff", PointWebMercator.ofPointCh(new PointCh(Ch1903.e(Math.toRadians(6.5790772),Math.toRadians(46.5218976)), Ch1903.n(Math.toRadians(6.5790772),Math.toRadians(46.5218976)))).getX());
    }

    @Test
    void xAtZoomLevel() {
        assertEquals("ff", new PointWebMercator(0.5182752012964598, 0.35366486371516465).xAtZoomLevel(19));
    }

    @Test
    void yAtZoomLevel() {
        assertEquals("ff", new PointWebMercator(0.5182752012964598, 0.35366486371516465).yAtZoomLevel(19));
    }

    @Test
    void lon() {
    }

    @Test
    void lat() {
    }

    @Test
    void toPointCh() {
        assertEquals(new PointCh(Ch1903.e(Math.toRadians(6.5790772), Math.toRadians(46.5218976)), Ch1903.n(Math.toRadians(6.5790772), Math.toRadians(46.5218976))), new PointWebMercator(0.518275214444, 0.353664894749).toPointCh());
    }

    @Test
    void x() {
    }

    @Test
    void y() {
    }
}