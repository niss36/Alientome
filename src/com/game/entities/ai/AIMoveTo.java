package com.game.entities.ai;

import com.game.entities.Entity;
import com.util.Direction;

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
    protected void reset() {
    }

    @Override
    public void act() {
        if (isRunning()) {
            if (entity.isDead()) fail();
            else move();
        }
    }

    /**
     * Private method to update the target <code>Entity</code>'s position each tick.
     */
    private void move() {

        if (destX != (int) entity.getX()) {

            int predictedX = (int) (entity.getX() +
                    Math.signum(entity.getMotionX()) * entity.getMotionX() * entity.getMotionX() +
                    entity.getMotionX() / 2);

            if((entity.getX() <= destX && predictedX >= destX) || (entity.getX() >= destX && predictedX <= destX)) {
                succeed();
                return;
            }

            entity.move(destX > entity.getX() ? Direction.RIGHT : Direction.LEFT);
        }
        if (isAtDestination()) succeed();
        else if (entity.collidedX) fail();
    }

    /**
     * @return whether the target <code>Entity</code> is at the destination X
     */
    private boolean isAtDestination() {
        return destX == entity.getX();
    }
}
