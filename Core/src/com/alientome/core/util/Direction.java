package com.alientome.core.util;

import com.alientome.core.vecmath.Constants;
import com.alientome.core.vecmath.Vec2;

public enum Direction {

    UP(Constants.UNIT_MINUS_Y),
    DOWN(Constants.UNIT_Y),
    LEFT(Constants.UNIT_MINUS_X),
    RIGHT(Constants.UNIT_X);

    public final Vec2 normal;
    public final boolean horizontal;

    Direction(Vec2 normal) {
        this.normal = normal;
        horizontal = normal.getY() == 0;
    }

    public static Direction requireHorizontal(String name) {
        Direction d = valueOf(name);
        if (d.horizontal)
            return d;
        throw new IllegalArgumentException(name + " is not horizontal.");
    }
}
