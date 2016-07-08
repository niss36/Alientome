package com.game.entities.ai;

import com.game.entities.Entity;
import com.game.entities.actions.Action;

public class AIDoAction extends AI {

    private final Action action;

    /**
     * Initialize the <code>AI</code>.
     *
     * @param entity the target <code>Entity</code>
     */
    public AIDoAction(Entity entity, Action action) {
        super(entity);
        this.action = action;
    }

    @Override
    public void act() {
        action.act();
    }

    @Override
    protected void reset() {

    }
}
