package ch.epfl.javelo.data;

import ch.epfl.javelo.Bits;
import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Q28_4;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.List;

/**
 * @author Louis ROCHE (345620)
 * @author Ambroise AIGUEPERSE (341890)
 */

/**
 * @param edgesBuffer contains for all edges of the graph: the direction,
 *                    the identity of the end node of the edge, the length of the edge,the positive drop and the identity of the attributs OSM set
 * @param profileIds  contains for all edges : the profile type and the identity of the first sample of the profile
 * @param elevations  contains all the profiles
 */
public record GraphEdges(ByteBuffer edgesBuffer, IntBuffer profileIds, ShortBuffer elevations) {
    private static final int LENGTH_OFFSET = 4;
    private static final int ELEVATION_OFFSET = 6;
    private static final int ATTRIBUTES_OFFSET = 8;
    /**
     * returns whether the given edge's orientation goes in the opposite direction of the OSM way which it provides from
     *
     * @param edgeId index of the edge. It is multiplied by 10 because there are 10 attributes per edge in edgesBuffer.
     * @return whether the edge's orientation goes in the opposite direction of the OSM way which it provides from
     * True if so, false otherwise.
     */
    public boolean isInverted(int edgeId) {
        return (edgesBuffer.getInt(edgeId * 10) < 0);
    }

    /**
     * returns the id of the target node of the given edge, whose id is edgeId.
     *
     * @param edgeId id of the edge to extract its destination from.
     * @return the id of the target node of the Edge at edgeId.
     */
    public int targetNodeId(int edgeId) {
        return (isInverted(edgeId) ? Bits.extractUnsigned(~edgesBuffer.getInt(edgeId * 10), 0, 31)
                : Bits.extractUnsigned(edgesBuffer.getInt(edgeId * 10), 0, 31));
    }

    /**
     * returns the length of the given edge, whose id is edgeId.
     *
     * @param edgeId id of the length to compute the length of.
     * @return the length of the edge whose id is edgeId.
     */
    public double length(int edgeId) {
        return Q28_4.asDouble(Short.toUnsignedInt(edgesBuffer.getShort(edgeId * 10 + LENGTH_OFFSET)));
    }

    /**
     * returns the elevation gain of the given edge, whose id is edgeId.
     *
     * @param edgeId id of the edge to extract the gain of elevation from.
     * @return the elevation gain of the edge whose id is edgeId.
     */

    public double elevationGain(int edgeId) {
        return Q28_4.asDouble(Short.toUnsignedInt(edgesBuffer.getShort(edgeId * 10 + ELEVATION_OFFSET)));
    }

    /**
     * returns whether the given edge, whose id is edgeId, has a profile
     *
     * @param edgeId id of the edge.
     * @return whether the edge whose id is edgeId has a profile.
     */
    public boolean hasProfile(int edgeId) {
        return (Bits.extractUnsigned(profileIds.get(edgeId), 30, 2) != 0);
    }

    /**
     * returns the samples of the OSM profiles of the given edge, whose id is edgeId.
     *
     * @param edgeId id of the edge.
     * @return the samples of OSM profiles of the edge whose id is edgeId.
     */

    public float[] profileSamples(int edgeId) {
        int nbOfProfiles = Math2.ceilDiv(edgesBuffer.getShort(edgeId * 10 + LENGTH_OFFSET),
                Q28_4.ofInt(2)) + 1;
        int profileId = Bits.extractSigned(profileIds.get(edgeId), 0, 30);
        float[] samples = new float[nbOfProfiles];
        float[] reverse;
        ProfileType type = List.of(ProfileType.values())
                .get(Bits.extractUnsigned(profileIds.get(edgeId), 30, 2));
        int count = 1;
        switch (type) {
            case NO_TYPE:
                return new float[]{};
            case RAW_TYPE:
                for (int i = 0; i < nbOfProfiles; i++) {
                    samples[i] = Q28_4.asFloat(Short.toUnsignedInt(elevations.get(profileId + i)));
                }
                break;
            case COMPRESSED_44:
                samples[0] = Q28_4.asFloat(elevations.get(profileId));
                for (int shortIndex = 1; shortIndex <= (nbOfProfiles - 1) / 2 + 1; shortIndex++) {
                    for (int sampleIndex = 1; sampleIndex >= 0; sampleIndex--) {
                        if (count < nbOfProfiles)
                            samples[count] = samples[count - 1] +
                                    Q28_4.asFloat(Bits.extractSigned(elevations.get(profileId + shortIndex), 8 * sampleIndex, 8));
                        ++count;
                    }
                }
                break;
            case COMPRESSED_04:
                samples[0] = Q28_4.asFloat(elevations.get(profileId));
                for (int shortIndex = 1; shortIndex <= nbOfProfiles - 1 / 4 + 1; shortIndex += 1) {
                    for (int hexIndex = 3; hexIndex >= 0; hexIndex--) {
                        if (count < nbOfProfiles)
                            samples[count] = samples[count - 1] +
                                    Q28_4.asFloat(Bits.extractSigned(elevations.get(profileId + shortIndex), 4 * hexIndex, 4));
                        ++count;
                    }
                }
                break;
        }
        if (isInverted(edgeId)) {
            reverse = new float[nbOfProfiles];
            for (int i = samples.length - 1; i >= 0; i--) {
                reverse[i] = samples[samples.length - i - 1];
            }
            return reverse;
        }
        return samples;
    }

    /**
     * returns the identity of the set of attributes attached to the given edge, whose id is edgeId.
     *
     * @param edgeId id of the edge to return the index from.
     * @return the identity of the set of attributes attached to the given edge, whose id is edgeId.
     */
    public int attributesIndex(int edgeId) {
        return Short.toUnsignedInt(edgesBuffer.getShort(edgeId * 10 + ATTRIBUTES_OFFSET));
    }

    /**
     * Enum for the different types a profile can have.
     * 0 : no type
     * 1 : raw type, uncompressed data
     * 2 : compressed data in the Q4.4 format
     * 3 : compressed data in the Q0.4 format
     */
    private enum ProfileType {
        NO_TYPE(0),
        RAW_TYPE(1),
        COMPRESSED_44(2),
        COMPRESSED_04(3);
        final int profileType;

        ProfileType(int type) {
            this.profileType = type;
        }
    }
}