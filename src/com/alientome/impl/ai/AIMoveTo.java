package com.alientome.impl.ai;

import com.alientome.core.util.Direction;
import com.alientome.game.ai.AI;
import com.alientome.game.entities.Entity;

/**
 * <code>AI</code> to move the target <code>Entity</code> to the specified destination.
 * Fails if the target <code>Entity</code> dies and succeeds if the destination is reached.
 */
public class AIMoveTo extends AI {

    private final int destX;

    /**
     * @param entity the target <code>Entity</code>
     * @param destX  the destination X coordinate.
     */
    public AIMoveTo(Entity entity, int destX) {

        super(entity);

        this.destX = destX;
    }

    @Override
    public void act() {
        if (isRunning()) {
            if (entity.isDead()) fail();
            else move();
        }
    }

    @Override
    public void reset() {
    }

    /**
     * Private method to update the target <code>Entity</code>'s position each tick.
     */
    private void move() {

        double x = entity.getPos().getX();
        if (destX != (int) x) {

            double vx = entity.getVelocity().getX();
            int predictedX = (int) (x + Math.signum(vx) * vx * vx + vx / 2);

            if ((x <= destX && predictedX >= destX) || (x >= destX && predictedX <= destX)) {
                succeed();
                return;
            }

            entity.move(destX > x ? Direction.RIGHT : Direction.LEFT);
        }
        if (isAtDestination()) succeed();
        else if (entity.collidedX) fail();
    }

    /**
     * @return whether the target <code>Entity</code> is at the destination X
     */
    private boolean isAtDestination() {
        return destX == entity.getPos().getX();
    }
}
