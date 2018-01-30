package com.alientome.game.actions;

import com.alientome.game.GameObject;
import com.alientome.game.level.Level;

public interface Action {

    default boolean shouldAct() {
        return true;
    }

    default void interrupt() {
    }

    default void update() {
    }

    default void act() {
    }

    void act(GameObject object);

    Level getLevel();

    default double getX() {
        return 0;
    }

    default double getY() {
        return 0;
    }
}
