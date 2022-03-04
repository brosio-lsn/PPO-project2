package ch.epfl.javelo.data;

import org.junit.jupiter.api.Test;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import static org.junit.jupiter.api.Assertions.*;

class GraphEdgesTest {
    @Test
    void test() {
        ByteBuffer edgesBuffer = ByteBuffer.allocate(10);
// Sens : inversé. Nœud destination : 12.
        //TODO ASK A MFING ASSISTANT FKEZGFKUHG FDVTYAZF DYTVAZFDFAZUVY
        edgesBuffer.putInt(0, ~12);
// Longueur : 0x10.b m (= 16.6875 m)
        edgesBuffer.putShort(4, (short) 0x10_b);
// Dénivelé : 0x10.0 m (= 16.0 m)
        edgesBuffer.putShort(6, (short) 0x10_0);
// Identité de l'ensemble d'attributs OSM : 1
        edgesBuffer.putShort(8, (short) 2022);

        IntBuffer profileIds = IntBuffer.wrap(new int[]{
                // Type : 3. Index du premier échantillon : 1.
                (3 << 30) | 1
        });

        ShortBuffer elevations = ShortBuffer.wrap(new short[]{
                (short) 0,
                (short) 0x180C, (short) 0xFEFF,
                (short) 0xFFFE, (short) 0xF000
        });

        GraphEdges edges =
                new GraphEdges(edgesBuffer, profileIds, elevations);

        assertTrue(edges.isInverted(0));
        assertEquals(12, edges.targetNodeId(0));
        assertEquals(16.6875, edges.length(0));
        assertEquals(16.0, edges.elevationGain(0));
        assertEquals(2022, edges.attributesIndex(0));
        float[] expectedSamples = new float[]{
                384.0625f, 384.125f, 384.25f, 384.3125f, 384.375f,
                384.4375f, 384.5f, 384.5625f, 384.6875f, 384.75f
        };
        assertArrayEquals(expectedSamples, edges.profileSamples(0));
    }
    @Test
    void testType2Louis() {
        /**
         * Created a specific test with correct values for a type 2 profile. It also tests if the program
         * takes the correct values in edgeBuffer.
         */
        ByteBuffer edgesBuffer = ByteBuffer.allocate(20);
// Sens : inversé. Nœud destination : 12.
        for (int i = 0; i < 10; i++) {
            edgesBuffer.putInt(i, 0b0);
        }
        //TODO ASK A MFING ASSISTANT FKEZGFKUHG FDVTYAZF DYTVAZFDFAZUVY
        edgesBuffer.putInt(10, 0b01100);
// Longueur : 0x12.b m (= 17.6875 m)
        edgesBuffer.putShort(14, (short) 0x11_b);
// Dénivelé : 0x10.0 m (= 26.75 m)
        edgesBuffer.putShort(16, (short) 0x1A_c);
// Identité de l'ensemble d'attributs OSM : ptdrrrr jsp j'ai mis au pif
        edgesBuffer.putShort(18, (short) 30921);

        IntBuffer profileIds = IntBuffer.wrap(new int[]{
                // Type : 2. Index du premier échantillon : 4.
                0b0,
                (2 << 30) | 4
        });

        ShortBuffer elevations = ShortBuffer.wrap(new short[]{
                (short)0, (short)0, (short)0,
                (short) 0,
                (short) 0x180C, (short) 0xBE0F,
                (short) 0x2E20, (short) 0xFFEE,
                (short) 0x2020, (short) 0x1000
        });

        GraphEdges edges =
                new GraphEdges(edgesBuffer, profileIds, elevations);

        assertFalse(edges.isInverted(1));
        assertEquals(12, edges.targetNodeId(1));
        assertEquals(17.6875, edges.length(1));
        assertEquals(26.75, edges.elevationGain(1));
        assertEquals(30921, edges.attributesIndex(1));
        float[] expectedSamples = new float[]{
                384.75f, 380.625f, 381.5625f, 384.4375f, 386.4375f, 386.375f,
                385.35f, 387.25f, 389.25f, 390.25f,
        };
        assertArrayEquals(expectedSamples, edges.profileSamples(0));
    }

}