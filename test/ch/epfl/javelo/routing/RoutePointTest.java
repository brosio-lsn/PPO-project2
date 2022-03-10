package ch.epfl.javelo.routing;

import ch.epfl.javelo.RNG;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;
import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;
import static java.lang.Double.NaN;

class RoutePointTest {

    @Test
    void withPositionShiftedBy() {
        RoutePoint damn = new RoutePoint(new PointCh(SwissBounds.MIN_E+500, SwissBounds.MIN_N+200), 500, 100);
        for(int i =0; i<1000;++i){
            double decalage = RNG.generateDouble(100000000);
            double position= RNG.generateDouble(100000000);
            assertEquals(new RoutePoint(new PointCh(SwissBounds.MIN_E+500, SwissBounds.MIN_N+200), position+decalage, 100), new RoutePoint(new PointCh(SwissBounds.MIN_E+500, SwissBounds.MIN_N+200), position, 100).withPositionShiftedBy(decalage));
            assertEquals(new RoutePoint(new PointCh(SwissBounds.MIN_E+500, SwissBounds.MIN_N+200), position-decalage, 100), new RoutePoint(new PointCh(SwissBounds.MIN_E+500, SwissBounds.MIN_N+200), position, 100).withPositionShiftedBy(-decalage));
        }
        assertEquals(new RoutePoint(new PointCh(SwissBounds.MIN_E+500, SwissBounds.MIN_N+200), 100, 100), new RoutePoint(new PointCh(SwissBounds.MIN_E+500, SwissBounds.MIN_N+200), 100, 100).withPositionShiftedBy(0));
    }

    @Test
    void min() {
        for (int i = 0; i < 1000; ++i) {
            double distanceRefthis = RNG.generateDouble(1000000);
            double distanceRefThat = RNG.generateDouble(1000000);
            RoutePoint p;
            if(distanceRefthis<=distanceRefThat){
                 p= new RoutePoint(new PointCh(SwissBounds.MIN_E + 500, SwissBounds.MIN_N + 200), 100, distanceRefthis);
            }
            else {
                p= new RoutePoint(new PointCh(SwissBounds.MIN_E + 500, SwissBounds.MIN_N + 200), 100, distanceRefThat);
            }
            assertEquals(p, new RoutePoint(new PointCh(SwissBounds.MIN_E + 500, SwissBounds.MIN_N + 200), 100, distanceRefthis).min(new RoutePoint(new PointCh(SwissBounds.MIN_E + 500, SwissBounds.MIN_N + 200), 100, distanceRefThat)));
        }
        assertEquals( new RoutePoint(new PointCh(SwissBounds.MIN_E + 500, SwissBounds.MIN_N + 200), 100, 50), new RoutePoint(new PointCh(SwissBounds.MIN_E + 500, SwissBounds.MIN_N + 200), 100, 50).min(new RoutePoint(new PointCh(SwissBounds.MIN_E + 500, SwissBounds.MIN_N + 200), 100, 50)));
    }

    @Test
    void testMin() {
        for (int i = 0; i < 1000; ++i) {
            double distanceRefthis = RNG.generateDouble(1000000);
            double distanceRefThat = RNG.generateDouble(1000000);
            RoutePoint p;
            if(distanceRefthis<=distanceRefThat){
                p= new RoutePoint(new PointCh(SwissBounds.MIN_E + 500, SwissBounds.MIN_N + 200), 100, distanceRefthis);
            }
            else {
                p= new RoutePoint(new PointCh(SwissBounds.MIN_E + 500, SwissBounds.MIN_N + 200), 100, distanceRefThat);
            }
            assertEquals(p, new RoutePoint(new PointCh(SwissBounds.MIN_E + 500, SwissBounds.MIN_N + 200), 100, distanceRefthis).min(new PointCh(SwissBounds.MIN_E + 500, SwissBounds.MIN_N + 200), 100, distanceRefThat));
        }
        boolean b = NaN<=10;
        assertEquals(1, new RoutePoint(new PointCh(SwissBounds.MIN_E + 500, SwissBounds.MIN_N + 200), NaN, NaN).min(new PointCh(SwissBounds.MIN_E + 500, SwissBounds.MIN_N + 200), 100, 10));
    }

}