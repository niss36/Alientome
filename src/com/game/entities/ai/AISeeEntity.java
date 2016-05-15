package com.game.entities.ai;

import com.game.entities.Entity;

/**
 * <code>AI</code> to determines whether the target <code>Entity</code> can see an other <code>Entity</code>.
 * Fails if <code>this.entity.distanceTo(this.lookingFor) > this.range</code>, if the line of sight is blocked
 * or if either <code>Entity</code> dies. Otherwise, succeeds.
 */
public class AISeeEntity extends AITest {

    private final Entity lookingFor;
    private final int range;

    /**
     * @param entity     the target <code>Entity</code>
     * @param lookingFor the <code>Entity</code> checked
     * @param range      the maximum sight range
     */
    public AISeeEntity(Entity entity, Entity lookingFor, int range) {
        super(entity);

        this.lookingFor = lookingFor;
        this.range = range;
    }

    @Override
    public void act() {
        if (entity.isDead()) fail();
        else if (lookingFor.isDead()) fail();

        else if (entity.distanceTo(lookingFor) > range) fail();

        else if (entity.level.canSeeEntity(entity, lookingFor)) succeed();
        else fail();
    }

    @Override
    protected void reset() {
    }
}
