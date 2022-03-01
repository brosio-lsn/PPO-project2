package ch.epfl.javelo;

import java.util.Random;

public class RNG {
    public static float generateFloat(int bound) {
        return new Random().nextFloat(bound);
    }
    public static double generateDouble(int bound) {
        return new Random().nextDouble(bound);
    }
    public static int generateIntNoBounds(int bound) {
        return new Random().nextInt((bound));
    }
}
