package com.util;

public enum Direction {

    UP,
    DOWN,
    LEFT,
    RIGHT;

    public static Direction toDirection(int key) {

        return key == Config.getInstance().getInt("Key.MoveLeft") ? LEFT : key == Config.getInstance().getInt("Key.MoveRight") ? RIGHT : null;
    }

    public Side toSide() {
        return this == LEFT ? Side.LEFT : this == RIGHT ? Side.RIGHT : this == UP ? Side.TOP : this == DOWN ? Side.BOTTOM : null;
    }
}
