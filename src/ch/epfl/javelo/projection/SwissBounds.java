package ch.epfl.javelo.projection;

/**
 * the bounds of Switzerland
 *
 * @author Ambroise Aigueperse (341890)
 * @author Louis Roche (345620)
 */

public final class SwissBounds {
    /**
     * private constructor to make the class non instantiable
     */
    private SwissBounds(){}

    /**
     * smallest E coordinate in switzerland
     */
    public static final double MIN_E =2485000;

    /**
     * largest E coordinate in switzerland
     */
    public static final double MAX_E = 2834000;

    /**
     * smallest N coordinate in switzerland
     */
    public static final double MIN_N = 1075000;

    /**
     * largest N coordinate in switzerland
     */
    public static final double MAX_N = 1296000;

    /**
     * swizerland's width
     */
    public static final double WIDTH = MAX_E-MIN_E;

    /**
     * swizerland's height
     */
    public static final double HEIGHT = MAX_N-MIN_N;

    /**
     * checks if a point is in Switzerland
     * @param e the E coordinate of the tested point
     * @param n the N coordinate of the tested point
     * @return true if the coordinates of the tested points are within Switzerland's bounds
     */
    public static boolean containsEN(double e, double n){
        return((e>=MIN_E && e<=MAX_E) && (n>=MIN_N && n<=MAX_N));
    }
}
