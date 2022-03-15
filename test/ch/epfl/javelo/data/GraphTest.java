package ch.epfl.javelo.data;

import ch.epfl.javelo.projection.PointCh;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.text.FieldPosition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GraphTest {

    @Test
    void loadFromThrowsIOExceptionForIllegalPath() {
        assertThrows(IOException.class, () -> Graph.loadFrom(Path.of("kfjaejbzf")));
    }
    @Test
    void loadFrom() throws IOException{
        Graph b = Graph.loadFrom(Path.of("lausanne"));
        Path filePath = Path.of("lausanne/nodes_osmid.bin");
        LongBuffer osmIdBuffer;
        try (FileChannel channel = FileChannel.open(filePath)) {
            osmIdBuffer = channel
                    .map(FileChannel.MapMode.READ_ONLY, 0, channel.size())
                    .asLongBuffer();
        }

        System.out.println(osmIdBuffer.get(1));
        System.out.println(osmIdBuffer.get(0));
        System.out.println(b.nodePoint(1));
        System.out.println(b.nodePoint(0));
        PointCh a = b.nodePoint(0);
        PointCh c = b.nodePoint(1);
        System.out.println("distance : " + c.distanceTo(a));
        System.out.println(b.nodeOutDegree(1));
        assertEquals(b.nodeCount(), osmIdBuffer.capacity());
        assertEquals(b.nodeClosestTo(new PointCh(2549212.9375, 1166183.5625), 0.1), 1);
        System.out.println(b.nodeOutEdgeId(2022,0));
        System.out.println(b.edgeAttributes(4095));
        System.out.println(b.edgeLength(1));
        System.out.println(b.edgeProfile(1));
        System.out.println(b.edgeElevationGain(1));
        System.out.println(b.edgeTargetNodeId(1));

    }

    @Test
    void nodeCount() {
    }

    @Test
    void nodePoint() {
    }

    @Test
    void nodeOutDegree() {
    }

    @Test
    void nodeOutEdgeId() {
    }

    @Test
    void nodeClosestTo() {
    }

    @Test
    void edgeTargetNodeId() {
    }

    @Test
    void edgeIsInverted() {
    }

    @Test
    void edgeAttributes() {
    }

    @Test
    void edgeLength() {
    }

    @Test
    void elevationGain() {
    }

    @Test
    void edgeProfile() {
    }
}