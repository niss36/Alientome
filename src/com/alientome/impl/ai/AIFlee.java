package com.alientome.impl.ai;

import com.alientome.core.util.Direction;
import com.alientome.game.ai.AI;
import com.alientome.game.entities.Entity;
import com.alientome.game.entities.EntityProjectile;

public class AIFlee extends AI {

    private final Entity fleeing;

    private final int fleeRangeSq;

    /**
     * @param entity    the target <code>Entity</code>
     * @param fleeing   the <code>Entity</code> to flee
     * @param fleeRange the maximum fleeing range
     */
    public AIFlee(Entity entity, Entity fleeing, int fleeRange) {
        super(entity);

        this.fleeing = fleeing;
        this.fleeRangeSq = fleeRange * fleeRange;
    }

    @Override
    public void act() {

        if (isRunning()) {

            if (entity.isDead()) fail();

            else if (fleeing.isDead()) succeed();

            else if (entity.distanceSqTo(fleeing) <= fleeRangeSq) {

                double fleeingX = fleeing.getPos().getX();
                double entityX = entity.getPos().getX();

                if (fleeingX > entityX) entity.move(Direction.LEFT);
                else if (fleeingX < entityX) entity.move(Direction.RIGHT);

                if (fleeingX != entityX) avoidObstacles();
            } else succeed();
        }
    }

    @Override
    public void reset() {
    }

    private void avoidObstacles() {

        //TODO deal with duplicated code

        if (entity.collidedX && entity.lastCollidedWith != fleeing && !(entity.lastCollidedWith instanceof EntityProjectile))
            entity.jump();

        else if (fleeing.getPos().getY() <= entity.getPos().getY()) {

            boolean b = entity.level.getMap().getBlockAbsCoordinates(
                    entity.getPos().getX() + entity.dimension.width / 2 + entity.getVelocity().getX(),
                    entity.getPos().getY() + entity.dimension.height + 1).canBeCollidedWith();

            if (!b) entity.jump();
        }
    }
}
