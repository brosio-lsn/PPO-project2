package ch.epfl.javelo;

import java.util.function.DoubleUnaryOperator;

/**
 * creates functions
 *
 * @author Louis ROCHE (345620)
 * @author Ambroise AIGUEPERSE (341890)
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

    public static DoubleUnaryOperator sampled(float[] samples, double xMax) {
        Preconditions.checkArgument(samples.length > 1 && xMax > 0);
        return new Sampled(samples, xMax);
    }

    /**
     * private innerclass used for the creation of a constant function
     */
    private record Constant(double constante) implements DoubleUnaryOperator {
        /**
         * returns the image of a given x coordinate
         *
         * @param x the x-coordinate
         * @return the image of the given x coordinate
         */
        @Override
        public double applyAsDouble(double x) {
            return this.constante;
        }
    }


    /**
     * private inner record used for the creation of a function with linear interpolation
     */
    private record Sampled(float[] samples, double xMax) implements DoubleUnaryOperator {
        /**
         * returns the image of a given x coordinate
         *
         * @param x the x-coordinate
         * @return the image of the given x coordinate
         */
        @Override
        public double applyAsDouble(double x) {
            //create those varaibles to not calculate them twice
            int samplesLength = samples.length;
            double lengthBetweenSamples = this.xMax / (samplesLength - 1);
            int precedentSampleIndex = (int) (x / lengthBetweenSamples);
            return (x >= xMax ? samples[samplesLength - 1] :
                    (x <= 0 ? samples[0] :
                            Math2.interpolate(samples[precedentSampleIndex], samples[precedentSampleIndex + 1],
                                    (x - lengthBetweenSamples * precedentSampleIndex) / lengthBetweenSamples)));
        }
    }
}