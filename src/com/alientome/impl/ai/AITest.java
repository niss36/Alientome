package com.alientome.impl.ai;

import com.alientome.game.ai.AI;
import com.alientome.game.entities.Entity;

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
