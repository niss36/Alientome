package com.game.entities.actions;

import com.game.GameObject;
import com.game.level.Level;
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

        if (state >= castTime && action.shouldAct()) {

            state = 0;
            handler.setAnimationEndListener(() -> handler.setAnimationUsed(0));
            return true;
        }

        handler.setAnimationUsed(useIndex);
        state++;

        return false;
    }

    @Override
    public void interrupt() {

        state = 0;
        handler.setAnimationUsed(0);

        action.interrupt();
    }

    @Override
    public void update() {
        action.update();
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
    public Level getLevel() {
        return action.getLevel();
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
