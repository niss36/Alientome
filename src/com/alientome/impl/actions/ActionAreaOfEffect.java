package com.alientome.impl.actions;

import com.alientome.game.GameObject;
import com.alientome.game.actions.Action;
import com.alientome.game.level.Level;

public class ActionAreaOfEffect implements Action {

    private final Action action;
    private final Class<? extends GameObject> affected;
    private final double range;
    private final GameObject[] excluded;

    public ActionAreaOfEffect(Action action, Class<? extends GameObject> affected, double range, GameObject... excluded) {

        this.action = action;
        this.affected = affected;
        this.range = range;
        this.excluded = excluded;
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

        getLevel().getObjectsInRange(affected, getX(), getY(), range, excluded).forEach(this::act);
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
