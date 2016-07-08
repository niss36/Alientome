package com.game.entities.actions;

import com.game.GameObject;

public class ActionCastTime implements Action {

    private final Action action;
    private final int castTime;
    private int state = 0;

    public ActionCastTime(Action action, int castTime) {

        this.action = action;
        this.castTime = castTime;
    }

    @Override
    public boolean shouldAct() {

        if (state == castTime && action.shouldAct()) {

            state = 0;
            return true;
        }

        state++;

        return false;
    }

    @Override
    public void act() {
        action.act();
    }

    @Override
    public void act(GameObject object) {
        action.act(object);
    }

    @Override
    public double getX() {
        return action.getX();
    }

    @Override
    public double getY() {
        return action.getY();
    }
}
