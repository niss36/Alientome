package com.alientome.core.util;

public enum Direction {

    UP(Vec2.UNIT_MINUS_Y),
    DOWN(Vec2.UNIT_Y),
    LEFT(Vec2.UNIT_MINUS_X),
    RIGHT(Vec2.UNIT_X);

    public final Vec2 normal;
    public final boolean horizontal;

    Direction(Vec2 normal) {
        this.normal = normal;
        horizontal = normal.y == 0;
    }

    public static Direction requireHorizontal(String name) {
        Direction d = valueOf(name);
        if (d.horizontal)
            return d;
        throw new IllegalArgumentException(name + " is not horizontal.");
    }
}
