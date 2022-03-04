package ch.epfl.javelo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Q28_4Test {

    @Test
    void ofIntForTrivialBit() {
        int trivialBit = 0b10011100;
        System.out.println(trivialBit);
        assertEquals(-6.25, Q28_4.ofInt(0b10011100));
    }
    //si ça marche pas c'est normal
    @Test
    void RNGTestofInt() {
        for (int i = 0; i < 100; i++) {
            int a = RNG.generateIntNoBounds(Integer.MAX_VALUE);
            System.out.println(a);
            String s = Integer.toBinaryString(a);
            s = "0b" + s;
            a = Integer.parseInt(s);
            if (Math.abs(a) > Math.pow(2, 31)) {
                int finalA = a;
                assertThrows(IllegalArgumentException.class, () -> {
                    Q28_4.ofInt(finalA);
                });
            }
         else {
                assertEquals(a*Math.pow(2, 4), Q28_4.ofInt(a));
            }
            System.out.println("coucou");
        }
    }

    @Test
    void asDoubleForTrivialBit() {
        for (int i = 0; i < 100; i++) {
            int a = RNG.generateIntNoBounds(0x7FFFFFFF);
            assertEquals((double) a / 16, Q28_4.asDouble(a));
        }
    }

    @Test
    void asFloatForTrivialBit() {
        int trivialBit = 0b110110;
        assertEquals((float)trivialBit/16, Q28_4.asFloat(trivialBit));
    }
}