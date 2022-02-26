package ch.epfl.javelo;

import java.util.Random;

public class RNG {
    public static float generateFloat() {
        return new Random().nextFloat(100);
    }
    public static double generateDouble() {
        return new Random().nextDouble(10000);
    }
    public static int generateIntNoBounds() {
        return new Random().nextInt((Integer.MAX_VALUE));
    }
}
