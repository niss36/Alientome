package com.game.entities.actions;

import com.game.GameObject;
import com.util.visual.AnimationsHandler;

public class ActionAnimation implements Action {

    private final Action action;
    private final AnimationsHandler handler;
    private final int useIndex;
    private final int defaultIndex;

    public ActionAnimation(Action action, AnimationsHandler handler, int useIndex, int defaultIndex) {

        this.action = action;
        this.handler = handler;
        this.useIndex = useIndex;
        this.defaultIndex = defaultIndex;
    }

    @Override
    public boolean shouldAct() {

        handler.setAnimationUsed(useIndex);

        if (action.shouldAct()) {
            handler.setAnimationUsed(defaultIndex);
            return true;
        }

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
