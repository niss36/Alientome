package com.game.entities.actions;

import com.game.GameObject;
import com.game.level.Level;

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
    public void act() {

        Level.getInstance().getObjectsInRange(affected, getX(), getY(), range, excluded)
                .forEach(this::act);
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
