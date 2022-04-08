package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.Preconditions;
import com.sun.jdi.DoubleValue;

import java.util.DoubleSummaryStatistics;
import java.util.function.DoubleUnaryOperator;

/**
 * represents the profile of a single or multiple itinerary
 *
 * @author Ambroise Aigueperse (341890)
 * @author Louis Roche (345620)
 */

public final class ElevationProfile {
    /**
     * the length of the profile (meters)
     */
    final private double length;
    /**
     * altitude samples evenly distributed over the profile
     */
    final private float[] elevationSamples;
    /**
     * contains the samples (initiated in constructor to not calculate it many times)
     */
    final private DoubleSummaryStatistics samples;
    /**
     * contains the function made of the samples (initiated in constructor to not calculate it many times)
     */
    final private DoubleUnaryOperator function;

    /**
     * constructs an instance of ElevationProfile
     * @param length the length of the profile (meters)
     * @param elevationSamples altitude samples evenly distributed over the profile
     */
    public ElevationProfile(double length, float[] elevationSamples){
        Preconditions.checkArgument(length>0 && elevationSamples.length>=2);
        this.length=length;
        this.elevationSamples = elevationSamples.clone();
        samples = initiateStats();
        //created here so that it isn't created multiple times
        function= Functions.sampled(this.elevationSamples, length);
    }

    /**
     * returns the length of the profile (meters)
     * @return returns the length of the profile (meters)
     */
    public double length(){return length;}

    /**
     * returns the minimum altitude of the profile
     * @return the minimum altitude of the profile
     */
    public double minElevation(){
        return samples.getMin();
    }

    /**
     * returns the maximum altitude of the profile
     * @return the maximum altitude of the profile
     */
    public double maxElevation(){
        return samples.getMax();
    }

    /**
     * returns the total positive vertical drop of the profile, in meters
     * @return the total positive vertical drop of the profile, in meters
     */
    public double totalAscent(){
        double totalAscent=0;
        for(int i=1; i<elevationSamples.length;++i)
            if(elevationSamples[i]-elevationSamples[i-1]>0)totalAscent+=elevationSamples[i]-elevationSamples[i-1];
        return totalAscent;
    }

    /**
     * returns the total negative elevation of the profile, in meters
     * @return the total negative elevation of the profile, in meters
     */
    public double totalDescent(){
        double totalDescent=0;
        for(int i=1; i<elevationSamples.length;++i)
            if(elevationSamples[i]-elevationSamples[i-1]<0)totalDescent+=elevationSamples[i]-elevationSamples[i-1];
        return Math.abs(totalDescent);
    }

    /**
     * returns the altitude of the profile at the given position (the first sample is returned when the position is negative, the last one when it is greater than the length)
     * @param position the position of which we want to get the altitude
     * @return the altitude of the profile at the given position (the first sample is returned when the position is negative, the last one when it is greater than the length)
     */
    public double elevationAt(double position){
        return function.applyAsDouble(position);
    }

    private DoubleSummaryStatistics initiateStats (){
        DoubleSummaryStatistics samples = new DoubleSummaryStatistics();
        for(float sample :this.elevationSamples) samples.accept(sample);
        return samples;
    }

}
