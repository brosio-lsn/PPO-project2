package ch.epfl.javelo;

import java.util.function.DoubleUnaryOperator;
//TODO chnge intern classes to records and do the other todo

/**
 * creates functions
 *
 * @author Louis ROCHE (345620)
 * @author Ambroise AIGUEPERSE (341860)
 */
public final class Functions {
    /**
     * private constructor to make the class non instantiable
     */
    private Functions() {
    }

    /**
     * returns a constant function whose value is always y
     *
     * @param y the constant value of the function
     * @return a function whose value is always y
     */
    public static DoubleUnaryOperator constant(double y) {
        return new Constant(y);
    }

    /**
     * returns a function obtained by linear interpolation between samples,
     * regularly spaced and covering the range from 0 to xMax
     *
     * @param samples used for the  linear interpolation
     * @param xMax    samples are covering the range from 0 to xMax
     * @return a function obtained by linear interpolation between samples,
     * * regularly spaced and covering the range from 0 to xMax
     */
    public static DoubleUnaryOperator constant(float[] samples, double xMax) {
        Preconditions.checkArgument(samples.length > 1 && xMax > 0);
        return new Sampled(samples, xMax);
    }

    /**
     * private innerclass used for the creation of a constant function
     */
    private static final class Constant implements DoubleUnaryOperator {
        /**
         * the value of the constant function
         */
        private final double contante;

        /**
         * constructor of the function
         *
         * @param y the value of the constant function
         */
        public Constant(double y) {
            this.contante = y;
        }

        /**
         * returns the image of a given x coordinate
         *
         * @param x the x-coordinate
         * @return the image of the given x coordinate
         */
        @Override
        public double applyAsDouble(double x) {
            return this.contante;
        }
    }

    /**
     * private innerclass used for the creation of a constant function with linear interpolation
     */
    private static final class Sampled implements DoubleUnaryOperator {
        /**
         * samples used for the  linear interpolation
         */
        private float[] samples;

        /**
         * samples are covering the range from 0 to xMax
         */
        private double xMax;

        /**
         * the constructor of the function
         *
         * @param samples used for the  linear interpolation
         * @param xMax    samples are covering the range from 0 to xMax
         */
        public Sampled(float[] samples, double xMax) {
            this.samples = samples;
            this.xMax = xMax;
        }


        /**
         * returns the image of a given x coordinate
         * @param x the x-coordinate
         * @return the image of the given x coordinate
         */
        @Override
        public double applyAsDouble(double x) {
            double result = Math2.clamp(0, x, samples.length - 1);
            if (!(result == x)) return samples[(int) result];
            else {
                double nb = this.xMax / (samples.length-1); //???
                int precedentSampleIndex = (int) Math.floor(x / nb);
                return Math2.interpolate(samples[precedentSampleIndex], samples[precedentSampleIndex + 1], Math.fma(x, 1 / nb, -precedentSampleIndex));
            }
        }
    }
}
