package ch.epfl.javelo;

import java.util.function.DoubleUnaryOperator;

public final class Functions {
    private Functions(){}

    public static DoubleUnaryOperator constant(double y) {
        return new Constant(y);
    }

    private static final class Constant implements DoubleUnaryOperator {
        private double contante;
        public Constant(double y) {
            this.contante=y;
        }

        @Override
        public double applyAsDouble(double x) {
            return this.contante;
        }
    }

}
