package com.jlmd.simpleneuralnetwork.neuralnetwork.utils;

import java.util.Random;

/**
 * @author jlmd
 */
public class Utils {

    /** Pseudo-random number generator instance. */
    private static final Random rand = new Random();

    /**
     * Returns a pseudo-random number between min and max, inclusive
     * @param min Minimum value
     * @param max Maximum value.  Must be greater than min
     * @return float between min and max, inclusive
     */
    public static float randFloat(float min, float max) {
        return rand.nextFloat() * (max - min) + min;
    }
}
