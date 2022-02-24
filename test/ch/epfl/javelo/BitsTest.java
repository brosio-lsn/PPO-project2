package ch.epfl.javelo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import ch.epfl.javelo.Bits;

class BitsTest {

    @Test
    void extractSigned() {
        int trivialBit = 0b00000000000000000000000000001100;
        System.out.println(Bits.extractSigned(trivialBit, 2, 2 ));
        assertEquals(0b11111111111111111111111111111111, Bits.extractSigned(trivialBit, 2, 2 ));
    }

    @Test
    void extractUnsigned() {
        int trivialBit = 0b0000000000000000000000000001100;
        System.out.println(Bits.extractSigned(trivialBit, 2, 2 ));
        assertEquals(0b00000000000000000000000000000011, Bits.extractUnsigned(trivialBit, 2, 2 ));
    }
}