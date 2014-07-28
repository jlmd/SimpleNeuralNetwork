package com.jlmd.simpleneuralnetwork.neuralnetwork.utils;

import java.util.Random;

/**
 * @author jlmd
 */
public class Utils {
    /**
     * Returns a pseudo-random number between min and max, inclusive
     * @param min Minimum value
     * @param max Maximum value.  Must be greater than min
     * @return float between min and max, inclusive
     */
    public static float randFloat(float min, float max) {
        Random rand = new Random();
        return rand.nextFloat() * (max - min) + min;
    }
}
