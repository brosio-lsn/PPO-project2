package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;

import java.util.ArrayList;
import java.util.List;

public class RouteBuilder {
    public static Route routeCreator(List<float[]> slt, double[] longueurs) {
        List<Edge> listePourLesRoutes = new ArrayList<>();
        for (int i = 0; i < slt.size()-1; i++) {
            listePourLesRoutes.add(new Edge(i, i+1, new PointCh(SwissBounds.MIN_E+i, SwissBounds.MIN_N+i),
                    new PointCh(SwissBounds.MIN_E+i+1, SwissBounds.MIN_N+i+1 + 20), longueurs[i], Functions.sampled(slt.get(i), longueurs[i])));
        }
        return new SingleRoute(listePourLesRoutes);
    }
    public static List<Edge> straightRouteFlat(double[] longueurs) {
        List<Edge> listePourLesRoutes = new ArrayList<>();
        double e = SwissBounds.MIN_E;
        for (int i = 0; i < longueurs.length; i++) {
            listePourLesRoutes.add(new Edge(i, i+1, new PointCh(e, SwissBounds.MIN_N),
                    new PointCh(e+longueurs[i], SwissBounds.MIN_N), longueurs[i], Functions.constant(1)));
            e += longueurs[i];
        }
        return listePourLesRoutes;
    }
}
