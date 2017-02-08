package com.game.entities.actions;

import com.game.GameObject;
import com.game.level.Level;

public class ActionCoolDown implements Action {


    private final Action action;
    private final int useCoolDown;
    private int coolDown = 0;

    public ActionCoolDown(Action action, int coolDown) {

        this.action = action;
        useCoolDown = coolDown;
    }

    @Override
    public boolean shouldAct() {

        if (coolDown == 0 && action.shouldAct()) {

            coolDown = useCoolDown;
            return true;
        }
        return false;
    }

    @Override
    public void interrupt() {
        action.interrupt();
    }

    @Override
    public void update() {

        if (coolDown > 0) coolDown--;

        action.update();
    }

    @Override
    public void act() {
        if (shouldAct()) action.act();
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
