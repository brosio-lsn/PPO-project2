package ch.epfl.javelo.data;

import org.junit.jupiter.api.Test;

import java.nio.IntBuffer;

import static org.junit.jupiter.api.Assertions.*;

class GraphNodesTest {

    @Test
    void edGeId() {
        IntBuffer b = IntBuffer.wrap(new int[]{
                8_680_150 << 4,
                3_501_666 << 4,
                0x1_888_456,
                2_600_000 << 4,
                1_200_000 << 4,
                0x2_000_1234
        });
        GraphNodes ns = new GraphNodes(b);
        assertEquals(0x1234, ns.edgeId(1, 0));
        assertEquals(0x1235, ns.edgeId(1, 1));
    }

    @Test
    void outDegree() {
        IntBuffer b = IntBuffer.wrap(new int[]{
                8_680_150 << 4,
                3_501_666 << 4,
                0x1_888_456,
                2_600_000 << 4,
                1_200_000 << 4,
                0x2_000_1234
        });
        GraphNodes ns = new GraphNodes(b);
        assertEquals(2, ns.outDegree(1));
    }

    @Test
    void nodeEN() {
        IntBuffer b = IntBuffer.wrap(new int[]{
                8_680_150 << 4,
                3_501_666 << 4,
                0x1_888_456,
                2_600_000 << 4,
                1_200_000 << 4,
                0x2_000_1234
        });
        GraphNodes ns = new GraphNodes(b);
        assertEquals(2, ns.count());
        assertEquals(2_600_000, ns.nodeE(1));
        assertEquals(1_200_000, ns.nodeN(1));
    }


}