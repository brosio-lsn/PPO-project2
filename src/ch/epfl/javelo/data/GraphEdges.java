package ch.epfl.javelo.data;

import ch.epfl.javelo.Bits;
import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.Q28_4;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

/**
 * @author Louis ROCHE (345620)
 * @author Ambroise AIGUEPERSE (341890)
 */

public record GraphEdges(ByteBuffer edgesBuffer, IntBuffer profileIds, ShortBuffer elevations) {
    private static final int LENGTH_OFFSET = 4;
    private static final int ELEVATIONGAIN_OFFSET = 6;
    private static final int ATTRIBUTESINDEX_OFFSET = 8;

    public GraphEdges{
       /* Preconditions.checkArgument(edgesBuffer.capacity() >= 10 && profileIds.capacity()>0);
        int type = Bits.extractUnsigned(profileIds.get(0), 30, 2);
        int nbOfProfiles = Math2.ceilDiv(edgesBuffer.getShort(LENGTH_OFFSET), Q28_4.ofInt(2))+1;
        switch (type) {
            case 1:
                Preconditions.checkArgument(elevations.capacity() >= nbOfProfiles);
                break;
            case 2:
                Preconditions.checkArgument(elevations.capacity() >= Math2.ceilDiv(nbOfProfiles, 2));
                break;
            case 3:
                Preconditions.checkArgument(elevations.capacity() >= 1 + Math2.ceilDiv(nbOfProfiles-1, 4));
        }

        */
    }
    //TODO faire une enum pour le switch

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
        return Q28_4.asDouble(Short.toUnsignedInt(edgesBuffer.getShort(edgeId * 10 + ELEVATIONGAIN_OFFSET)));
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
                Q28_4.ofInt(2))+1;
        int type = (Bits.extractUnsigned(profileIds.get(edgeId), 30, 2));
        int profileId = Bits.extractSigned(profileIds.get(edgeId), 0, 30);
        float[] samples = new float[nbOfProfiles];
        float[] reverse = new float[nbOfProfiles];
        int count = 1;
        switch (type) {
            case 0:
                return new float[]{};
            case 1:
                for (int i = 0; i < nbOfProfiles; i++) {
                    samples[i] = Q28_4.asFloat(Short.toUnsignedInt(elevations.get(profileId + i)));
                }
                break;
            case 2:
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
            case 3:
                samples[0] = Q28_4.asFloat(elevations.get(profileId));
                for (int i = 1; i <= nbOfProfiles - 1 / 4 + 1; i += 1) {
                    for (int j = 3; j >= 0; j--) {
                        if (count < nbOfProfiles)
                            samples[count] = samples[count - 1] +
                                    Q28_4.asFloat(Bits.extractSigned(elevations.get(profileId + i), 4 * j, 4));
                        ++count;
                    }
                }
                break;
        }
        if (isInverted(edgeId)) {
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
        return Short.toUnsignedInt(edgesBuffer.getShort(edgeId * 10 + ATTRIBUTESINDEX_OFFSET));
    }
}