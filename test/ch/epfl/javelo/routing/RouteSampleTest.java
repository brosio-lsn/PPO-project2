package ch.epfl.javelo.routing;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class RouteSampleTest implements Route {
    public List<Edge> unoDosTres = new ArrayList<>();
    public RouteSampleTest() throws IOException {
        Graph routeGraph = Graph.loadFrom(Path.of("lausanne"));
        unoDosTres.add(Edge.of(routeGraph, 835836139, 835836216,  69929283));
    }

    @Override
    public int indexOfSegmentAt(double position) {
        return 0;
    }

    @Override
    public double length() {
        return 0;
    }

    @Override
    public List<Edge> edges() {
        return  unoDosTres;
    }

    @Override
    public List<PointCh> points() {
        return null;
    }

    @Override
    public PointCh pointAt(double position) {
        return null;
    }

    @Override
    public int nodeClosestTo(double position) {
        return 0;
    }

    @Override
    public RoutePoint pointClosestTo(PointCh point) {
        return null;
    }

    @Override
    public double elevationAt(double position) {
        return this.edges().get(0).elevationAt(position);
    }
}
