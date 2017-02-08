package com.util;

public enum Direction {

    UP,
    DOWN,
    LEFT,
    RIGHT;

    public Vec2 normal() {
        return this == LEFT ? Vec2.UNIT_MINUS_X : this == RIGHT ? Vec2.UNIT_X : this == UP ? Vec2.UNIT_MINUS_Y : this == DOWN ? Vec2.UNIT_Y : null;
    }
}
