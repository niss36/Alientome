package com.alientome.impl.ai;

import com.alientome.game.ai.AI;
import com.alientome.game.entities.Entity;

/**
 * <code>AI</code> to make the target <code>Entity</code> wander randomly.
 * Fails if the associated <code>AIMoveTo</code> or <code>AIIdle</code> fails.
 * Succeeds if the associated <code>AIIdle</code> succeeds.
 */
public class AIWander extends AI {

    private final AIIdle aiIdle = new AIIdle(entity);
    private AIMoveTo aiMoveTo;

    /**
     * @param entity the target <code>Entity</code>
     */
    public AIWander(Entity entity) {
        super(entity);
    }

    @Override
    public void act() {

        if (aiMoveTo.isRunning()) {
            aiMoveTo.act();

            if (aiMoveTo.isSuccess() || entity.collidedX) aiIdle.start();
            else if (aiMoveTo.isFailure()) fail();
        } else if (aiIdle.isRunning()) {
            aiIdle.act();

            if (aiIdle.isSuccess()) succeed();
            else if (aiIdle.isFailure()) fail();
        }
    }

    @Override
    public void reset() {
        aiMoveTo = new AIMoveTo(entity, (int) entity.getPos().x + entity.level.getRandom().nextInt(50) - 25);
        aiIdle.reset();
    }

    @Override
    public void start() {
        reset();
        super.start();
        aiMoveTo.start();
    }
}
