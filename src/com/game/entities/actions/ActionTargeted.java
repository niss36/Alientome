package com.game.entities.actions;

import com.game.GameObject;

public class ActionTargeted implements Action {

    private final Action action;
    private final GameObject target;

    public ActionTargeted(Action action, GameObject target) {

        this.action = action;
        this.target = target;
    }

    @Override
    public boolean shouldAct() {
        return action.shouldAct();
    }

    @Override
    public void act() {
        act(target);
    }

    @Override
    public void act(GameObject object) {
        action.act(object);
    }

    @Override
    public double getX() {
        return target.getPos().x;
    }

    @Override
    public double getY() {
        return target.getPos().y;
    }
}
