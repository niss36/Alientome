package com.util;

public class Util {

    /**
     * Not instantiable
     */
    private Util() {

    }

    public static int clamp(int value, int minVal, int maxVal) {

        return value < minVal ? minVal : value > maxVal ? maxVal : value;
    }

    public static double scale(double oldValue, double oldMin, double oldMax, double newMin, double newMax) {

        return (((oldValue - oldMin) * (newMax - newMin)) / (oldMax - oldMin)) + newMin;
    }
}
