package com.util;

public enum Side {

    TOP,
    BOTTOM,
    RIGHT,
    LEFT;

    public static Side toSide(double x, double y) {

        return ((x > 0) ? LEFT : ((x < 0) ? RIGHT : ((y > 0) ? TOP : ((y < 0) ? BOTTOM : null))));
    }
}
