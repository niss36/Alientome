package com.game.entities.ai;

import com.game.entities.Entity;
import com.game.level.LevelUtils;
import com.util.AxisAlignedBB;

public class AIEntityAbove extends AITest {

    private final Entity other;
    private final int horizontalTolerance;

    /**
     * @param entity              the target <code>Entity</code>
     * @param other               the <code>Entity</code> to check
     * @param horizontalTolerance the maximum horizontal distance
     */
    public AIEntityAbove(Entity entity, Entity other, int horizontalTolerance) {
        super(entity);

        this.other = other;
        this.horizontalTolerance = horizontalTolerance;
    }

    @Override
    public void act() {
        if (isRunning()) {

            if (entity.isDead()) fail();

            else if (other.isDead()) fail();

            else if (entity.getPos().y + entity.dim.height > other.getPos().y + other.dim.height
                    && Math.abs(entity.getPos().x - other.getPos().x) <= 40
                    && LevelUtils.canSeeEntity(entity, other)) {

                AxisAlignedBB expanded = other.getNextBoundingBox().expand(horizontalTolerance, 300);

                if (expanded.intersects(entity.getNextBoundingBox())) succeed();
                else fail();

            } else fail();
        }
    }

    @Override
    protected void reset() {

    }
}
