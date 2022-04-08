package ch.epfl.javelo;

/**
 * Extracts bit vectors of given sizes and lengths
 *
 * @author Louis ROCHE (345620)
 * @author Ambroise AIGUEPERSE (341890)
 */
public final class Bits {
    /**
     * Constructor of the Bits class, which is private so that the class is not instantiable.
     */
    private Bits() {
    }

    /**
     * Extracts the signed expression of the bit of length 'length', which starts at the 'start'th bit from
     * the bit vector 'value'.
     *
     * @param value  bit vector to extract the result from.
     * @param start  # of the bit to start the extraction from.
     * @param length length of the bit to be extracted, which is also the difference between the # of the
     *               starting bit, and that of the ending bit.
     * @return the signed expression of the bit of length 'length' which starts at the 'start'th bit
     * from the bit vector 'value'
     * @throws IllegalArgumentException if the length of the bit vector is not strictly positive,
     *                                  if the start position is negative, or if the sum start + length is not within
     *                                  the range [0, 32]
     */
    public static int extractSigned(int value, int start, int length) {
        Preconditions.checkArgument(start >= 0 && (start + length) <= 32 && length > 0);
        return (value << (32 - (start + length))) >> (32 - length);
    }

    /**
     * Extract the unsigned expression of the bit of length 'length', which starts at the 'start'th bit from
     * the bit vector 'value'.
     *
     * @param value  bit vector to extract the result from.
     * @param start  # of the bit to start the extraction from.
     * @param length length of the bit to be extracted, which is also the difference between the # of the
     *               starting bit, and that of the ending bit.
     * @return the unsigned expression of the bit of length 'length' which starts at the 'start'th bit
     * from the bit vector 'value'
     * @throws IllegalArgumentException if the length of the bit vector is not within [0, 31],
     *                                  if the start position is negative, or if the sum start + length is not within
     *                                  the range [0, 32]
     */
    public static int extractUnsigned(int value, int start, int length) {
        Preconditions.checkArgument(start >= 0 && (start + length) <= 32 && length < 32 && length > 0);
        return (value << (32 - (start + length)) >>> (32 - length));
    }
}
