package com.game.entities.actions;

import com.game.GameObject;
import com.game.level.Level;

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
