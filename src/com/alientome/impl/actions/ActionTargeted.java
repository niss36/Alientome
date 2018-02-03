package com.alientome.impl.actions;

import com.alientome.game.GameObject;
import com.alientome.game.actions.Action;
import com.alientome.game.level.Level;

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
    public void interrupt() {
        action.interrupt();
    }

    @Override
    public void update() {
        action.update();
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
    public Level getLevel() {
        return action.getLevel();
    }

    @Override
    public double getX() {
        return target.getPos().getX();
    }

    @Override
    public double getY() {
        return target.getPos().getY();
    }
}
