package ch.epfl.javelo;

/**
 * @author Louis ROCHE (345620)
 * @author Ambroise AIGUEPERSE (341860)
 */
public final class Math2 {
    private Math2() {}

    /**
     * returns the ceiling of x/y.
     * @throws IllegalArgumentException if y <= 0 or x < 0
     * @param x dividend
     * @param y divisor
     * @return the ceiling of x/y.
     */
    public static int ceilDiv(int x, int y) {
        Preconditions.checkArgument( x >= 0 && y > 0);
        return ((x+y-1)/y);
        //sfsfdf
    }

    /**
     * returns the ordinate of the point that belongs to the straight line crossing the points
     * (0, y0) and (1, y1), and of abscissa x.
     * @param y0 ordinate at which the straight line crosses the x = 0 axis.
     * @param y1 ordinate at which the straight line crosses the x = 1 axis.
     * @param x abscissa of the point we desire to get the ordinate of
     * @return the ordinate of the point that belongs to the straight line crossing the points
     * (0, y0) and (1, y1), and of abscissa x. Is uses the formula
     * y = ax + b, where b = y0 and a = y1 - y0.
     */
    public static double interpolate(double y0, double y1, double x) {
        return (Math.fma((y1 - y0),x, y0));
    }

    /**
     * limits v to the interval [min, max]
     * @throws IllegalArgumentException if min> max
     * @param min lower bound of the interval
     * @param v parameter to be limited to the interval [min, max]
     * @param max upped bound of the interval
     * @return max if v > max; min if v < min; v if v is an element of [min, max]
     */
    public static int clamp(int min, int v, int max) {
        Preconditions.checkArgument(min <= max);
        return (v>max ? max : ((v < min) ? min: v));
    }

    /**
     * limits v to the interval [min, max]
     * @throws IllegalArgumentException if min> max
     * @param min lower bound of the interval
     * @param v parameter to be limited to the interval [min, max]
     * @param max upped bound of the interval
     * @return max if v > max; min if v < min; v if v is an element of [min, max]
     */
    public static double clamp(double min, double v, double max) {
        //faisable en une ligne avec min et max de maths
        Preconditions.checkArgument(min <= max);
        return (v>max ? max : ((v < min) ? min: v));
    }

    /**
     * returns the argument of the hyperbolic sine that equals to x.
     * @param x the result of the hyperbolic sine.
     * @return the argument of the hyperbolic sine that equals to x.
     */
    public static double asinh(double x) {
        return Math.log(x + Math.sqrt(1 + Math.pow(x, 2)));
    }

    /**
     * returns the scalar product of the vector u(uX, uY) with the vector v(vX, vY)
     * @param uX abscissa of the u vector
     * @param uY ordinate of the u vector
     * @param vX abscissa of the v vector
     * @param vY ordinate of the v vector
     * @return the scalar product of the vector u with the vector v.
     */
    public static double dotProduct(double uX, double uY, double vX, double vY){
        return Math.fma(uX, vX, uY*vY);
    }

    /**
     * Returns the square of the norm of the vector u(uX, uY). It is
     * computed using the scalar product of the vector with itself.
     * @param uX abscissa of the vector
     * @param uY ordinate of the vector
     * @return the square of the norm of the vector, which has uX as abscissa and uY as ordinate.
     */
    public static double squaredNorm(double uX, double uY) {
        return dotProduct(uX, uY, uX, uY);
    }

    /**
     * Returns the norm of the vector u(uX, uY).
     * @param uX abscissa of the vector u
     * @param uY ordinate of the vector u
     * @return The norm of the vector u, which is the square root of its squaredNorm.
     */
    public static double norm(double uX, double uY) {
        return Math.hypot(uX, uY);
    }

    /**
     * returns the length of the projection of the vector AP that goes from the point A(aX, aY) to the point
     * P(pX, pY) on the vector AB that goes from the point A(aX, aY) to the point B(bX, bY).
     * @param aX abscissa of the point A.
     * @param aY ordinate of the point A.
     * @param bX abscissa of the point B.
     * @param bY ordinate of the point B.
     * @param pX abscissa of the point P.
     * @param pY ordinate of the point P.
     * @return The length of the projection of AP on AB, which is computed by the formula (AP.AB)/norm(AB)
     */
    public static double projectionLength(double aX, double aY, double bX, double bY, double pX, double pY) {
        /*double xAP = pX - aX;
        double yAP = pY - aY;
        double xAB = bX - aX;
        double yAB = bY - aY;*/
        return (dotProduct(pX - aX, pY - aY, bX - aX, bY - aY)/norm(bX - aX, bY - aY));
    }
}
