package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.Preconditions;

import java.util.DoubleSummaryStatistics;

/**
 * represents the profile of a single or multiple itinerary
 *
 * @author Ambroise Aigueperse (341890)
 * @author Louis Roche (345620)
 */

public class ElevationProfile {
    /**
     * the length of the profile (meters)
     */
    //TODO demander si c'est final
    private final double length;
    /**
     * altitude samples evenly distributed over the profile
     */
    private final float[] elevationSamples;

    /**
     * constructs an instance of ElevationProfile
     *
     * @param length           the length of the profile (meters)
     * @param elevationSamples altitude samples evenly distributed over the profile
     */
    public ElevationProfile(double length, float[] elevationSamples) {
        Preconditions.checkArgument(length > 0 && elevationSamples.length >= 2);
        this.length = length;
        this.elevationSamples = elevationSamples;
    }

    /**
     * returns the length of the profile (meters)
     *
     * @return returns the length of the profile (meters)
     */
    public double length() {
        return length;
    }

    /**
     * returns the minimum altitude of the profile
     *
     * @return the minimum altitude of the profile
     */
    public double minElevation() {
        DoubleSummaryStatistics s = new DoubleSummaryStatistics();
        for (float sample : elevationSamples) s.accept(sample);
        return s.getMin();
    }

    /**
     * returns the maximum altitude of the profile
     *
     * @return the maximum altitude of the profile
     */
    //TODO demander assistant si je devrais pas construire s dans le constructeur pour pas le recalculer a chaque fois
    public double maxElevation() {
        DoubleSummaryStatistics s = new DoubleSummaryStatistics();
        for (float sample : elevationSamples) s.accept(sample);
        return s.getMax();
    }

    /**
     * returns the total positive vertical drop of the profile, in meters
     *
     * @return the total positive vertical drop of the profile, in meters
     */
    public double totalAscent() {
        float totalAscent = 0;
        for (int i = 1; i < elevationSamples.length; ++i)
            if (elevationSamples[i] - elevationSamples[i - 1] > 0)
                totalAscent += elevationSamples[i] - elevationSamples[i - 1];
        return totalAscent;
    }

    /**
     * returns the total negative elevation of the profile, in meters
     *
     * @return the total negative elevation of the profile, in meters
     */
    public double totalDescent() {
        float totalDescent = 0;
        for (int i = 1; i < elevationSamples.length; ++i)
            if (elevationSamples[i] - elevationSamples[i - 1] < 0)
                totalDescent += elevationSamples[i] - elevationSamples[i - 1];
        return -totalDescent;
    }

    /**
     * returns the altitude of the profile at the given position
     * (the first sample is returned when the position is negative, the last one when it is greater than the length)
     *
     * @param position the position of which we want to get the altitude
     * @return the altitude of the profile at the given position
     * (the first sample is returned when the position is negative, the last one when it is greater than the length)
     */
    //TODO si je construit la fonction au debut ou pas (pour pas recalculer a chaque fois)
    public double elevationAt(double position) {
        return Functions.sampled(elevationSamples, length).applyAsDouble(position);
    }
}
