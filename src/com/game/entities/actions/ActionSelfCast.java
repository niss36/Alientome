package com.game.entities.actions;

import com.game.GameObject;
import com.game.entities.Entity;

public class ActionSelfCast implements Action {

    private final Action action;
    private final Entity entity;

    public ActionSelfCast(Action action, Entity entity) {

        this.action = action;
        this.entity = entity;
    }

    @Override
    public boolean shouldAct() {
        return action.shouldAct();
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
        return entity.getPos().x;
    }

    @Override
    public double getY() {
        return entity.getPos().y;
    }
}
