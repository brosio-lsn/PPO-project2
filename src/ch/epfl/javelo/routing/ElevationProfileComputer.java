package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class ElevationProfileComputer {
    private ElevationProfileComputer() {
    }

    /**
     * returns the profile along the given itinerary.
     *
     * @param route         given itinerary
     * @param maxStepLength maximum spacing between each sample
     * @return the profile along the given itinerary.
     */
    public static ElevationProfile elevationProfile(Route route, double maxStepLength) {
        Preconditions.checkArgument(maxStepLength > 0);
        int profileSpacing = (int) Math.ceil(route.length() / maxStepLength);
        float[] samples = new float[(int) route.length() / profileSpacing];
        for (int i = 0; i < route.length(); i++) {
            if (!(profileSpacing * i > route.length())) {
                samples[i] = ((float) route.elevationAt(profileSpacing * i));
            }
        }
        return new ElevationProfile(route.length(), fillTheHoles(samples));
    }

    private static float[] fillTheHoles(float[] samples) {
        float firstValidValue = Float.NaN;
        float lastValidValue = 0;
        int lastValidValueIndex = 0;
        //filling front and back holes
        for (int i = 0; i < samples.length; i++) {
            //TODO créer une méthode pour opti ?
            if (!Float.isNaN(samples[i])) {
                lastValidValue = samples[i];
                lastValidValueIndex = i;
                if (Float.isNaN(firstValidValue)) {
                    firstValidValue = samples[i];
                }
            }
            Arrays.fill(samples, 0, i, firstValidValue);
        }
        if (Float.isNaN(firstValidValue)) return new float[samples.length];
        Arrays.fill(samples, lastValidValueIndex, samples.length - 1, lastValidValue);
        //filling intermediate holes
        int beginningIndex = 0;
        int finishIndex = 0;
        boolean finishStreak = false;
        for (int i = 0; i < samples.length; i++) {
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
                for (int j = beginningIndex; j < finishIndex; j++) {
                    samples[j] = (float) Math2.interpolate(samples[beginningIndex], samples[finishIndex],
                            (double) (j - beginningIndex) / ((finishIndex) - beginningIndex));
                }
                finishStreak = false;
            }
        }
        return samples;
    }
}
