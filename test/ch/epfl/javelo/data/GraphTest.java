package ch.epfl.javelo.data;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;
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
        int[] nodesBuffer = new int[100000];
        byte[] sectorsBuffer = new byte[100000];
        int[] profileBuffer = new int[100000];
        byte[] edgesBuffer = new byte[1000000];
        short[] elevationBuffer = new short[1000000];
        long[] attributesBuffer = new long[100000];

    Graph b = Graph.loadFrom(Path.of("lausanne"));
    try(InputStream edgesRead = new FileInputStream("lausanne/edges.bin");
    InputStream nodesRead = new FileInputStream("lausanne/nodes.bin");
    InputStream elevationRead = new FileInputStream("lausanne/elevations.bin");
    InputStream profileIdsRead = new FileInputStream("lausanne/profile_ids.bin");
    InputStream attributesRead = new FileInputStream("lausanne/attributes.bin");
    InputStream sectorsRead = new FileInputStream("lausanne/sectors.bin")) {
        int j = 0;
        int reader;
        boolean pasPasse = false;
        while ((reader = nodesRead.read()) != -1) {
            if (!pasPasse) nodesBuffer[0] = reader; pasPasse = true;
            nodesBuffer[(j+1)/32] = nodesBuffer[(j+1)/32] <<1 | reader;
            ++j;

        }
        j = 0; pasPasse = false;
        while((reader = edgesRead.read()) != -1) {
            if (!pasPasse) edgesBuffer[0] = (byte)reader; pasPasse = true;
            edgesBuffer[(j)/8] = (byte)(edgesBuffer[(j)/8]<<1 | reader);
            ++j;
        }


       j= 0;pasPasse = false;
        while((reader = sectorsRead.read()) != -1) {
            if (!pasPasse) sectorsBuffer[0] = (byte)reader; pasPasse = true;
            sectorsBuffer[(j+1)/8] = (byte)(sectorsBuffer[(j+1)/8]<<1 | reader);
            ++j;
        }


        j=0;pasPasse = false;
        while((reader = elevationRead.read()) != -1) {

            if (!pasPasse) elevationBuffer[0] = (short)reader; pasPasse = true;
            elevationBuffer[(j+1)/16] = (short)(elevationBuffer[(j+1)/16]<<1 | reader);
            ++j;
        }
        j=0;pasPasse = false;

        while ((reader = profileIdsRead.read()) != -1) {
            if (!pasPasse) profileBuffer[0] = reader; pasPasse = true;
            profileBuffer[(j+1)/32] =(elevationBuffer[(j+1)/32]<<1 | reader);

        }


        j = 0;pasPasse = false;
        while ((reader = attributesRead.read()) != -1) {
            if (!pasPasse) attributesBuffer[0] = reader; pasPasse = true;
            attributesBuffer[j/64] =0;
                    // (attributesBuffer[j/64]<<1 | reader);
            j++;
        }

        List<AttributeSet> attributeSets = new ArrayList<>();
        for (int i = 0; i < 9999; i++) {
            attributeSets.add(new AttributeSet(attributesBuffer[i]));
        }
        Graph test = new Graph(new GraphNodes(IntBuffer.wrap(nodesBuffer)), new GraphSectors(ByteBuffer.wrap(sectorsBuffer)),
                new GraphEdges(ByteBuffer.wrap(edgesBuffer), IntBuffer.wrap(profileBuffer), ShortBuffer.wrap(elevationBuffer)),
                        attributeSets);
        assertEquals(b.nodePoint(0), test.nodePoint(0));
        //assertEquals(b.edgeLength(0), test.edgeLength(0));
       //assertEquals(b.edgeProfile(0), test.edgeProfile(0));
        //assertEquals(b.edgeAttributes(0), test.edgeAttributes(0));
        //assertEquals(b.edgeTargetNodeId(0), test.edgeTargetNodeId(0));
    }
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