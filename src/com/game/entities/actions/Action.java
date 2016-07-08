package com.game.entities.actions;

import com.game.GameObject;

public interface Action {

    default boolean shouldAct() {
        return true;
    }

    default void act() {
    }

    void act(GameObject object);

    default double getX() {
        return 0;
    }

    default double getY() {
        return 0;
    }
}
