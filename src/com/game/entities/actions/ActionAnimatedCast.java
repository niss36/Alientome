package com.game.entities.actions;

import com.game.GameObject;
import com.util.visual.AnimationsHandler;

public class ActionAnimatedCast implements Action {

    private final Action action;
    private final int castTime;
    private final AnimationsHandler handler;
    private final int useIndex;
    private int state = 0;

    public ActionAnimatedCast(Action action, int castTime, AnimationsHandler handler, int useIndex) {

        this.action = action;
        this.castTime = castTime;
        this.handler = handler;
        this.useIndex = useIndex;
    }

    @Override
    public boolean shouldAct() {

        if (state == castTime && action.shouldAct()) {

            state = 0;
            handler.setAnimationUsed(0);
            return true;
        }

        handler.setAnimationUsed(useIndex);
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
