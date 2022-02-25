package ch.epfl.javelo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FunctionsTest {

    @Test
    void constant() {
        assertEquals(Functions.constant(3).applyAsDouble(4), 3);
    }

    @Test
    void sampled() {
        assertEquals((Functions.sampled(new float []{0f, 4f, 4f}, 8)).applyAsDouble(8), 4);
        assertEquals((Functions.sampled(new float []{0f, 4f, 4f}, 8)).applyAsDouble(6), 4);
        assertEquals((Functions.sampled(new float []{0f, 4f, 4f}, 8)).applyAsDouble(0), 0);
        assertEquals((Functions.sampled(new float []{0f, 4f, 4f}, 8)).applyAsDouble(2), 2);
        assertEquals((Functions.sampled(new float []{0f, 4f, 4f}, 8)).applyAsDouble(-4), 0);
        assertEquals((Functions.sampled(new float []{0f, 4f, 4f}, 8)).applyAsDouble(18), 4);
    }
    @Test
    void throwsExceptionWhenxMaxNegative() {
        assertThrows(IllegalArgumentException.class, () -> {
            Functions.sampled(new float [] {0f, 4f}, -1);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            Functions.sampled(new float [] {0f}, 1);
        });
    }
}