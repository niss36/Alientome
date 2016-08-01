package com.game.entities.ai;

import com.game.entities.Entity;
import com.game.level.LevelUtils;

/**
 * <code>AI</code> to determines whether the target <code>Entity</code> can see an other <code>Entity</code>.
 * Fails if <code>this.entity.distanceTo(this.lookingFor) > this.range</code>, if the line of sight is blocked
 * or if either <code>Entity</code> dies. Otherwise, succeeds.
 */
public class AISeeEntity extends AITest {

    private final Entity lookingFor;
    private final int rangeSq;

    /**
     * @param entity     the target <code>Entity</code>
     * @param lookingFor the <code>Entity</code> checked
     * @param range      the maximum sight range
     */
    public AISeeEntity(Entity entity, Entity lookingFor, int range) {
        super(entity);

        this.lookingFor = lookingFor;
        this.rangeSq = range * range;
    }

    @Override
    public void act() {
        if (entity.isDead()) fail();
        else if (lookingFor.isDead()) fail();

        else if (entity.distanceSqTo(lookingFor) > rangeSq) fail();

        else if (LevelUtils.canSeeEntity(entity, lookingFor)) succeed();
        else fail();
    }

    @Override
    protected void reset() {
    }
}
