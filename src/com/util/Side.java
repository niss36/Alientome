package com.util;

public enum Side {

    TOP,
    BOTTOM,
    RIGHT,
    LEFT;

    public boolean isVertical() {

        return this == TOP || this == BOTTOM;
    }
}
