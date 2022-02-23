package ch.epfl.javelo;

/**
 * the preconditions
 *
 * @author Ambroise Aigueperse (341890)
 * @author Louis Roche (345620)
 */

public final class Preconditions {

    /**
     * private constructor to make the class non instantiable
     */
    private Preconditions() {}


    /**
     * throws an error if the given argument is false
     * @param shouldBeTrue the argument that should be true
     * @throws IllegalArgumentException if the argument is false
     */
    public static void checkArgument(boolean shouldBeTrue){
        if(!shouldBeTrue) throw  new IllegalArgumentException();
    }
}