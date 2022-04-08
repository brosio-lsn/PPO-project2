package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;

import java.util.Arrays;

/**
 * Computes the elevationProfile of an itinerary
 *
 * @author ROCHE Louis (345620)
 * @author AIGUEPERSE Ambroise (341890)
 */
public final class ElevationProfileComputer {
    /**
     * Constructor of ElevationProfileComputer, which is private to make the class non-instantiable.
     */
    private ElevationProfileComputer() {
    }

    /**
     * returns the profile along the given itinerary.
     *
     * @param route         given itinerary
     * @param maxStepLength maximum spacing between each sample
     * @return the profile along the given itinerary.
     * @throws IllegalArgumentException if xMax is negative or null.
     */
    public static ElevationProfile elevationProfile(Route route, double maxStepLength) {
        Preconditions.checkArgument(maxStepLength > 0);
        int nbOfSamples = (int) Math.ceil(route.length() / maxStepLength) + 1;
        float[] samples = new float[nbOfSamples];
        double stepLength = route.length() / (nbOfSamples - 1);
        for (int i = 0; i < samples.length; i++) {
            samples[i] = ((float) route.elevationAt(stepLength * i));
        }
        return new ElevationProfile(route.length(), fillTheHoles(samples));
    }

    /**
     * Fills the NaN values with interpolated values from previous or next samples.
     *
     * @param samples tab to fill the NaN values in.
     * @return the filled tab with no more NaN values in it.
     */
    private static float[] fillTheHoles(float[] samples) {
        float firstValidValue =Float.NaN;
        int firstValidValueIndex=0;
        float lastValidValue = 0;
        int lastValidValueIndex = 0;
        //filling front and back holes
        for (int i = 0; i < samples.length; i++) {
            if (!Float.isNaN(samples[i])) {
                lastValidValue = samples[i];
                lastValidValueIndex = i;
                if (Float.isNaN(firstValidValue)) {
                    //we use the Float.isNaN(firstValidValue) to determine whether a non-NaN value in the samples
                    //has been found.
                    firstValidValue = samples[i];
                    firstValidValueIndex = i;
                    Arrays.fill(samples, 0, i, firstValidValue);
                }
            }
        }
        if (Float.isNaN(firstValidValue)) return new float[samples.length];
        Arrays.fill(samples, lastValidValueIndex, samples.length, lastValidValue);
        //filling intermediate holes
        return fillMiddleHoles(samples, firstValidValueIndex, lastValidValueIndex);
    }

    /**
     * fills 'holes' in between two non-NaN values by interpolating them.
     * @param samples array to fills the holes in
     * @param firstValidValueIndex the index of the first Non-NaN value.
     * @param lastValidValueIndex the index of the last non-NaN value.
     * @return the filled array.
     */
    private static float[] fillMiddleHoles(float[] samples, int firstValidValueIndex, int lastValidValueIndex) {
        int beginningIndex = 0;
        int finishIndex = 0;
        boolean finishStreak = false;
        for (int i = firstValidValueIndex+1; i < lastValidValueIndex; i++) {
            if (Float.isNaN(samples[i])) {
                if (!Float.isNaN(samples[i - 1])) {
                    beginningIndex = i - 1;
                }
                if (!Float.isNaN(samples[i + 1])) {
                    finishIndex = i + 1;
                    finishStreak = true;
                }
            }
            if (finishStreak) {
                for (int indexInBetween = beginningIndex + 1; indexInBetween < finishIndex; indexInBetween++) {
                    samples[indexInBetween] = (float) Math2.interpolate(samples[beginningIndex], samples[finishIndex],
                            (double) (indexInBetween - beginningIndex) / ((finishIndex) - beginningIndex));
                    //here the ratio in the range [0, 1] of a given point between two valid samples
                    // is given by (indexInBetween - beginningIndex) / ((finishIndex) - beginningIndex)
                    // since it represents the 'index' distance from the beginning point to the given point.
                }
                finishStreak = false;
                i = finishIndex;
            }
        }
        return samples;
    }
}
