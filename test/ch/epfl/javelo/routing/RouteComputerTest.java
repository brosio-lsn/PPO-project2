package ch.epfl.javelo.routing;

import ch.epfl.javelo.KmlPrinter;
import ch.epfl.javelo.data.Graph;

import java.io.IOException;
import java.nio.LongBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class RouteComputerTest {
    public static void main(String[] args) throws IOException {
        Path filePath = Path.of("lausanne/nodes_osmid.bin");
        LongBuffer osmIdBuffer=null;
        try (FileChannel channel = FileChannel.open(filePath)) {
            osmIdBuffer = channel
                    .map(FileChannel.MapMode.READ_ONLY, 0, channel.size())
                    .asLongBuffer();
        }
        catch (Exception e ){
            System.out.print("rip");
        }
        System.out.println(osmIdBuffer.get(0));
        System.out.println(osmIdBuffer.get(600));

        Graph g = Graph.loadFrom(Path.of("lausanne"));
        CostFunction cf = new CityBikeCF(g);
        RouteComputer rc = new RouteComputer(g, cf);
        Route r = rc.bestRouteBetween(159049, 117669);
        KmlPrinter.write("javelo.kml", r);
    }

}