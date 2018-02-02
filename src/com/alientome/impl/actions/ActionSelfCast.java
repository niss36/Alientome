package com.alientome.impl.actions;

import com.alientome.game.GameObject;
import com.alientome.game.actions.Action;
import com.alientome.game.entities.Entity;
import com.alientome.game.level.Level;

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
    public void interrupt() {
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
        return entity.getPos().getX();
    }

    @Override
    public double getY() {
        return entity.getPos().getY();
    }
}
