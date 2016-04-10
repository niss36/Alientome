package com.util;

import java.awt.event.KeyEvent;

public enum Direction {

    UP(KeyEvent.VK_W),
    DOWN(KeyEvent.VK_S),
    LEFT(KeyEvent.VK_A),
    RIGHT(KeyEvent.VK_D);

    private final int key;

    Direction(int key) {

        this.key = key;
    }

    public static Direction toDirection(int key) {

        return key == LEFT.key ? LEFT : key == RIGHT.key ? RIGHT : null;
    }

    public Side toSide() {
        return this == LEFT ? Side.LEFT : this == RIGHT ? Side.RIGHT : this == UP ? Side.TOP : this == DOWN ? Side.BOTTOM : null;
    }
}
