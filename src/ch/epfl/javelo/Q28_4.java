package ch.epfl.javelo;

import java.time.OffsetTime;

/**
 * convert numbers between the Q28.4 representation
 * and other representations
 *
 * @author Louis ROCHE (345620)
 * @author Ambroise AIGUEPERSE (341890)
 */
public final class Q28_4 {
    private static final int OFFSET = 4;
    /**
     * private constructor to make the class non instantiable
     */
    private Q28_4() {
    }

    /**
     * returns the Q28.4 value corresponding to the given integer
     *
     * @param i the given integer
     * @return the Q28.4 value corresponding to the given integer
     */
    public static int ofInt(int i) {
        return i << OFFSET;
    }

    /**
     * returns the double value equal to the given Q28.4 value
     *
     * @param q28_4 the given Q28.4 value
     * @return the double value equal to the given Q28.4 value
     */
    public static double asDouble(int q28_4) {
        return Math.scalb((double) q28_4, -OFFSET);
    }

    /**
     * returns the float value equal to the given Q28.4 value
     *
     * @param q28_4 the given Q28.4 value
     * @return the float value equal to the given Q28.4 value
     */
    public static float asFloat(int q28_4) {
        return Math.scalb(q28_4, -OFFSET);
    }
}
