package com.alientome.impl.ai;

import com.alientome.game.actions.Action;
import com.alientome.game.ai.AI;
import com.alientome.game.entities.Entity;

public class AIDoAction extends AI {

    private final Action action;

    public AIDoAction(Entity entity, Action action) {
        super(entity);
        this.action = action;
    }

    @Override
    public void act() {

        action.update();

        action.act();
    }

    @Override
    public void reset() {

    }
}
