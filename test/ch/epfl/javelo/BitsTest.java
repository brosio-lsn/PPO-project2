package ch.epfl.javelo;

import ch.epfl.javelo.projection.PointWebMercator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import ch.epfl.javelo.Bits;

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
        System.out.println(Bits.extractUnsigned(trivialBit, 0, 3 ));
        assertEquals(0b111, Bits.extractUnsigned(trivialBit, 0, 3 ));
        int bizarreBit = 0b11111110111111011111110000001010;
        System.out.println(0b011111101111110111111100000010100);
        System.out.println(bizarreBit);
        assertEquals(32, Bits.extractUnsigned(trivialBit, 31, 0 ));
    }

    @Test
    void extractSignedThrows() {
        int trivialBit = 0b0000000000000000000000000001100;
        assertThrows(IllegalArgumentException.class, () -> {
            Bits.extractUnsigned(trivialBit, 5, 29 );
        });
    }
}