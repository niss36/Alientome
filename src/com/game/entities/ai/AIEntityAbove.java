package com.game.entities.ai;

import com.game.entities.Entity;
import com.util.AxisAlignedBB;

public class AIEntityAbove extends AITest {

    private final Entity other;

    /**
     * @param entity the target <code>Entity</code>
     * @param other  the <code>Entity</code> to check
     */
    public AIEntityAbove(Entity entity, Entity other) {
        super(entity);

        this.other = other;
    }

    @Override
    public void act() {
        if (isRunning()) {

            if (entity.isDead()) fail();

            else if (other.isDead()) fail();

            else if (entity.getY() + entity.dim.height > other.getY() + other.dim.height && entity.level.canSeeEntity(entity, other)) {

                AxisAlignedBB expanded = other.getNextBoundingBox().expand(10, 300);

                if (expanded.intersectsWith(entity.getNextBoundingBox())) succeed();
                else fail();

            } else fail();
        }
    }

    @Override
    public void reset() {

    }
}
