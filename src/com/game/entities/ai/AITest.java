package com.game.entities.ai;

import com.game.entities.Entity;

public abstract class AITest extends AI {

    /**
     * @param entity the target <code>Entity</code>
     */
    AITest(Entity entity) {
        super(entity);
    }

    public boolean result() {

        start();
        act();

        return isSuccess();
    }
}
