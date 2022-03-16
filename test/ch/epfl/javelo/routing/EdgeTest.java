package ch.epfl.javelo.routing;
import ch.epfl.javelo.Functions;
import ch.epfl.javelo.Math2;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;
import org.junit.jupiter.api.Test;

import java.util.function.DoubleUnaryOperator;

import static org.junit.jupiter.api.Assertions.*;

public class EdgeTest {

    @Test
    void positionClosestTo() {
        float[] yeet = {30f, 31f, 30.5f, 34f, 29f, 30f, 35f, 29.5f, 33f, 31f,60f};
        PointCh fromPoint= new PointCh(SwissBounds.MIN_E+500, SwissBounds.MIN_N+200);
        PointCh toPoint= new PointCh(SwissBounds.MIN_E+510, SwissBounds.MIN_N+205);
        double length = Math2.norm((toPoint.n()-fromPoint.n()),(toPoint.e()-fromPoint.e()));
        DoubleUnaryOperator yo = Functions.sampled(yeet,length);
        Edge edge = new Edge(0,50,fromPoint,toPoint, length, yo);
        double a = (toPoint.n()-fromPoint.n())/(toPoint.e()-fromPoint.e());
        double expected=length/2.0;
        PointCh pointOndroite = edge.pointAt(expected);
        PointCh pointNotOnDroite = new PointCh(SwissBounds.MIN_E+53, -1/a*(SwissBounds.MIN_E+53)+pointOndroite.n()+1/a*pointOndroite.e());
        assertEquals(expected,edge.positionClosestTo(pointNotOnDroite), 10E-2);

        expected=length;
        pointOndroite = edge.pointAt(expected);
        pointNotOnDroite = new PointCh(SwissBounds.MIN_E+53, -1/a*(SwissBounds.MIN_E+53)+pointOndroite.n()+1/a*pointOndroite.e());
        assertEquals(expected,edge.positionClosestTo(pointNotOnDroite), 10E-3);

        expected=0;
        pointOndroite = edge.pointAt(expected);
        pointNotOnDroite = new PointCh(SwissBounds.MIN_E+53, -1/a*(SwissBounds.MIN_E+53)+pointOndroite.n()+1/a*pointOndroite.e());
        assertEquals(expected,edge.positionClosestTo(pointNotOnDroite), 10E-3);

        expected=-length*0.75;
        pointOndroite = edge.pointAt(expected);
        pointNotOnDroite = new PointCh(SwissBounds.MIN_E+51, -1/a*(SwissBounds.MIN_E+51)+pointOndroite.n()+1/a*pointOndroite.e());
        assertEquals(expected,edge.positionClosestTo(pointNotOnDroite), 10E-3);

        expected=2*length;
        pointOndroite = edge.pointAt(expected);
        pointNotOnDroite = new PointCh(SwissBounds.MIN_E+51, -1/a*(SwissBounds.MIN_E+51)+pointOndroite.n()+1/a*pointOndroite.e());
        assertEquals(expected,edge.positionClosestTo(pointNotOnDroite), 10E-3);
    }

    @Test
    void AltitudeAtGivenPosition() {
        float[] yeet = {30f, 31f, 30.5f, 34f, 29f, 30f, 35f, 29.5f, 33f, 31f,60f};
        PointCh fromPoint= new PointCh(SwissBounds.MIN_E+500, SwissBounds.MIN_N+200);
        PointCh toPoint= new PointCh(SwissBounds.MIN_E+510, SwissBounds.MIN_N+200);
        double length = Math2.norm((toPoint.n()-fromPoint.n()),(toPoint.e()-fromPoint.e()));
        DoubleUnaryOperator yo = Functions.sampled(yeet,length);
        Edge edge = new Edge(0,50,fromPoint,toPoint, length, yo);
        assertEquals(45.5, edge.elevationAt(9.5));
        assertEquals(60, edge.elevationAt(92));
        assertEquals(30, edge.elevationAt(-10));
        assertEquals(30, edge.elevationAt(0));
        assertEquals(60f, edge.elevationAt(length));

        float[] yeet2 = {28f, 30f, 30.5f, 34f, 26f, 30f, 37f, 29.5f, 32f, 31f,80f};
        PointCh fromPoint2= new PointCh(SwissBounds.MIN_E+510, SwissBounds.MIN_N+205);
        PointCh toPoint2= new PointCh(SwissBounds.MIN_E+522, SwissBounds.MIN_N+211);
        double length2 = Math2.norm((toPoint2.n()-fromPoint2.n()),(toPoint2.e()-fromPoint2.e()));
        DoubleUnaryOperator yo2 = Functions.sampled(yeet2,length);
        Edge edge2 = new Edge(50,62,fromPoint2,toPoint2, length2, yo2);

        assertEquals(80f, edge2.elevationAt(length));
    }
}


