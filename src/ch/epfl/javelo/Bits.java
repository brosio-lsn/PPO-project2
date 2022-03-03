package ch.epfl.javelo;

import ch.epfl.javelo.Preconditions;
//TODO check that the throw error condtions are met in the methods
/**
 * @author Louis ROCHE (345620)
 * @author Ambroise AIGUEPERSE (341890)
 */
public final class Bits {
    /**
     * Default constructor of the Bits class, which is private so that the class is not instanciable.
     */
    private Bits(){}

    /**
     * Extracts the signed expression of the bit of length 'length', which starts at the 'start'th bit from
     * the bit vector 'value'.
     * @param value bit vector to extract the result from.
     * @param start # of the bit to start the extraction from.
     * @param length length of the bit to be extracted, which is also the difference between the # of the
     *               starting bit, and that of the ending bit.
     * @return the signed expression of the bit of length 'length' which starts at the 'start'th bit
     * from the bit vector 'value'
     */
    //TODO demander à un assistant pour preconditions,"la plage de bits décrite par les arguments start et length est valide si et seulement si elle est totalement incluse dans l'intervalle allant de 0 à 31 (inclus)."
    public static int extractSigned(int value, int start, int length) {
        Preconditions.checkArgument(start >= 0 && (start+length-1) <= 31);
        return (value << (32-(start+length))) >> (32-length);
    }

    /**
     * Extract the unsigned expression of the bit of length 'length', which starts at the 'start'th bit from
     * the bit vector 'value'.
     * @param value bit vector to extract the result from.
     * @param start # of the bit to start the extraction from.
     * @param length length of the bit to be extracted, which is also the difference between the # of the
     *               starting bit, and that of the ending bit.
     * @return the unsigned expression of the bit of length 'length' which starts at the 'start'th bit
     * from the bit vector 'value'
     */
    //TODO urgent demamder a un assistant pour le 31 32 truc
    public static int extractUnsigned(int value, int start, int length) {
        Preconditions.checkArgument(start >= 0 && (start+length-1) <= 31 && length <= 32);
        return (value << (32-(start+length)) >>> (32-length));
    }
}
