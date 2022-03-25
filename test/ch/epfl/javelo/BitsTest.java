package ch.epfl.javelo;

import ch.epfl.javelo.projection.PointWebMercator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import ch.epfl.javelo.Bits;

import java.nio.LongBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;

class BitsTest {

    @Test
    void extractSigned() {
        int trivialBit = 0b11001010111111101011101010111110;
        System.out.println(Bits.extractSigned(trivialBit, 8, 4 ));
        assertEquals(0b11111111111111111111111111111010, Bits.extractSigned(trivialBit, 8, 4 ));
    }

    @Test
    void extractUnsigned() {
        int trivialBit = 0b11101110111;
        //System.out.println(Bits.extractUnsigned(trivialBit, 0, 3 ));
        assertEquals(0b111, Bits.extractUnsigned(trivialBit, 0, 3 ));
        int bizarreBit = 0xF_000_0000;
        int bit= 0b1111_0000_0000_0000_0000_0000_0000_0000;
        System.out.println(bit);
        System.out.println(bizarreBit);
        assertEquals(1, Bits.extractUnsigned(bizarreBit, 31, 1 ));
    }

    @Test
    void extractSignedThrows() {
        int trivialBit = 0b0000000000000000000000000001100;
        assertThrows(IllegalArgumentException.class, () -> {
            Bits.extractUnsigned(trivialBit, 5, 29 );
        });

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
        System.out.println(osmIdBuffer.get(201));
    }
}