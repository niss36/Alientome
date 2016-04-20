package com.game.entities.ai;

import com.game.Block;
import com.game.entities.Entity;
import com.util.Direction;

public class AIFlee extends AI {

    private final Entity fleeing;

    private final int fleeRange;

    /**
     * @param entity    the target <code>Entity</code>
     * @param fleeing   the <code>Entity</code> to flee
     * @param fleeRange the maximum fleeing range
     */
    public AIFlee(Entity entity, Entity fleeing, int fleeRange) {
        super(entity);

        this.fleeing = fleeing;
        this.fleeRange = fleeRange;
    }

    @Override
    public void act() {

        if (isRunning()) {

            if (entity.isDead()) fail();

            else if (fleeing.isDead()) succeed();

            else if (entity.distanceTo(fleeing) <= fleeRange) {

                if (fleeing.getX() > entity.getX()) entity.move(Direction.LEFT);
                else if (fleeing.getX() < entity.getX()) entity.move(Direction.RIGHT);

                if (fleeing.getX() != entity.getX()) avoidObstacles();
            } else succeed();
        }
    }

    private void avoidObstacles() {

        if (entity.collidedX && entity.lastCollidedWith != fleeing) entity.jump();

        else if (fleeing.getY() <= entity.getY()) {

            boolean b = new Block((int) ((entity.getX() + entity.dim.width / 2 + entity.getMotionX()) / Block.width),
                    (int) ((entity.getY() + entity.dim.height + 1) / Block.width)).isOpaque();

            if (!b) entity.jump();
        }
    }

    @Override
    public void reset() {
        start();
    }
}
