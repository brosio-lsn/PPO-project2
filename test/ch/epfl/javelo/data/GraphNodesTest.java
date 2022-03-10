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
        IntBuffer node1 = IntBuffer.wrap(new int[]{
                2_600_000 << 4,
                1_200_000 << 4,
                0x2_000_1234,
                1200 << 4,
                1200 <<4,
                2,
                4_325_045 << 4,
                2_245_125 << 4,
                0x3_125_1246,
                0,
                0,
                0

        });
        GraphNodes n1 = new GraphNodes(node1);

        @Test
        public void countIsEqualToWhatWasAsked(){
            GraphNodes node1 = new GraphNodes(IntBuffer.allocate(10));
            assertEquals(10/3, node1.count());
            GraphNodes node2 = new GraphNodes(IntBuffer.allocate(2));
            assertEquals(2/3, node2.count());
            GraphNodes node3 = new GraphNodes(IntBuffer.allocate(1000));
            assertEquals(1000/3, node3.count());
        }

        @Test
        public void countWorksForEmptyBuffer() {
            GraphNodes node1 = new GraphNodes(IntBuffer.allocate(0));
            assertEquals(0/3, node1.count());
        }
        @Test
        public void countThrowsOnNegativeValue(){
            //pas besoin la classe buffer le fait automatiquement
        /*
        GraphNodes node1 = new GraphNodes(IntBuffer.allocate(-10));
        assertThrows(IllegalArgumentException.class, () -> {
            node1.count();
        });

         */
        }

        @Test
        public void nodeEWorksOnRandomValues(){
            assertEquals(2_600_000,n1.nodeE(0));
            assertEquals(1200,n1.nodeE(1));
            assertEquals(4_325_045,n1.nodeE(2));
            assertEquals(0,n1.nodeE(3));

        }
        /*
            @Test
            public void nodeEThrowsOnNegativeValues(){
                assertThrows(IllegalArgumentException.class, () -> {
                    n1.nodeE(-1);
                });
            }

        */
        @Test
        public void nodeNWorksOnRandomValues(){
            assertEquals(1_200_000, n1.nodeN(0));
            assertEquals(1200,n1.nodeN(1));
            assertEquals(2_245_125 ,n1.nodeN(2));
            assertEquals(0,n1.nodeN(3));
        }

        @Test
        public void nodeNThrowsOnNegativeValues(){

        }

        @Test
        public void outDegreeWorks(){
            assertEquals(2, n1.outDegree(0));
            assertEquals(0, n1.outDegree(1));
            assertEquals(3, n1.outDegree(2));
            assertEquals(0, n1.outDegree(3));

        }

        @Test
        public void edgeIdWorks() {
            assertEquals(0x1234, n1.edgeId(0, 0));
            assertEquals(0x1235, n1.edgeId(0, 1));

            assertThrows(AssertionError.class, () -> {
                n1.edgeId(1, 0);
            });

            System.out.println(0x1234);
            System.out.println(0x2_00_1234);


            assertEquals(19206726, n1.edgeId(2, 0));
            assertEquals(19206727, n1.edgeId(2, 1));
            assertEquals(19206728, n1.edgeId(2, 2));
            ;
        }
        @Test
        void readingTrivialBuffer() {
            IntBuffer b = IntBuffer.wrap(new int[]{
                    2_600_000 <<4,
                    1_200_000 <<4,
                    0x2_000_1234
            });
            GraphNodes ns = new GraphNodes(b);
            assertEquals(1, ns.count());
            assertEquals(2_600_000, ns.nodeE(0));
            assertEquals(1_200_000, ns.nodeN(0));
            assertEquals(2, ns.outDegree(0));
            assertEquals(0x1234, ns.edgeId(0, 0));
            assertEquals(0x1235, ns.edgeId(0, 1));
        }


        @Test
        void reading2NodesBuffer(){
            IntBuffer c = IntBuffer.wrap(new int[]{
                    2_600_000 <<4,
                    1_200_000 <<4,
                    0x2_000_1234,
                    2_500_000 <<4,
                    1_100_000 <<4,
                    0x5_000_1234
            });
            GraphNodes ns = new GraphNodes(c);


            assertEquals(2, ns.count());
            assertEquals(2_500_000, ns.nodeE(1));
            assertEquals(1_100_000, ns.nodeN(1));
            assertEquals(5, ns.outDegree(1));
            assertEquals(0x1234, ns.edgeId(1, 0));
            assertEquals(0x1238, ns.edgeId(1, 4));

        }

    }
