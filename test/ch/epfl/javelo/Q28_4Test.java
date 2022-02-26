package ch.epfl.javelo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Q28_4Test {

    @Test
    void ofIntForTrivialBit() {
        int trivialBit = 0b10011100;
        System.out.println(trivialBit);
        assertEquals(-6.25, Q28_4.ofInt(-100));
    }

    @Test
    void asDoubleForTrivialBit() {
        int trivialBit = 0b110110;
        assertEquals((double)trivialBit/16, Q28_4.asDouble(trivialBit));
    }

    @Test
    void asFloatForTrivialBit() {
        int trivialBit = 0b110110;
        assertEquals((float)trivialBit/16, Q28_4.asDouble(trivialBit));
    }
}