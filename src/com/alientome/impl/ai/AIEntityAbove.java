package com.alientome.impl.ai;

import com.alientome.core.collisions.AxisAlignedBoundingBox;
import com.alientome.game.collisions.StaticBoundingBox;
import com.alientome.game.entities.Entity;

public class AIEntityAbove extends AITest {

    private final Entity other;
    private final int horizontalTolerance;
    private final int verticalThreshold = 300;

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
            else if (doTest()) succeed();
            else fail();
        }
    }

    @Override
    public void reset() {

    }

    private boolean doTest() {

        AxisAlignedBoundingBox entityBB = entity.getNextBoundingBox();
        AxisAlignedBoundingBox otherBB = other.getNextBoundingBox();

        AxisAlignedBoundingBox areaUnder = new StaticBoundingBox(
                otherBB.getMinX() - horizontalTolerance,
                otherBB.getMaxY(),
                otherBB.getMaxX() + horizontalTolerance,
                otherBB.getMaxY() + verticalThreshold);

        return entityBB.getMinY() > otherBB.getMaxY()
                && areaUnder.intersects(entityBB)
                && entity.level.sightTest(entity, other);
    }
}
