package com.alientome.impl.ai;

import com.alientome.game.ai.AI;
import com.alientome.game.entities.Entity;

/**
 * <code>AI</code> to idle an <code>Entity</code> for a random amount of time.
 * Fails if the target <code>Entity</code> dies and succeeds if the time to wait has been reached.
 */
public class AIIdle extends AI {

    private int time;

    /**
     * @param entity the target <code>Entity</code>
     */
    public AIIdle(Entity entity) {
        this(entity, entity.level.getRandom().nextInt(25) + 25);
    }

    public AIIdle(Entity entity, int time) {
        super(entity);

        this.time = time;
    }

    @Override
    public void act() {

        if (isRunning()) {
            if (entity.isDead()) fail();
            else if (time == 0) succeed();
            else time--;
        }

    }

    @Override
    public void reset() {
        time = entity.level.getRandom().nextInt(25) + 25;
    }
}
