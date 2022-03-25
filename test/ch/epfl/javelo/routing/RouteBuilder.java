package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;

import java.util.ArrayList;
import java.util.List;

public class RouteBuilder {
    public static Route routeCreator(List<float[]> slt, double[] longueurs) {
        double sum = 0;
        List<Edge> listePourLesRoutes = new ArrayList<>();
        for (double longueur : longueurs) {
            sum += longueur;
        }
        for (int i = 0; i < slt.size(); i++) {
            listePourLesRoutes.add(new Edge(0, 10, new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N),
                    new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N + 20), longueurs[i], Functions.sampled(slt.get(i), longueurs[i])));
        }
        return new SingleRoute(listePourLesRoutes);
    }
}
