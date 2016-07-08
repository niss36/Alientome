package com.game.entities.ai;

import com.game.blocks.Block;
import com.game.entities.Entity;
import com.game.level.LevelMap;
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

                if (fleeing.getPos().x > entity.getPos().x) entity.move(Direction.LEFT);
                else if (fleeing.getPos().x < entity.getPos().x) entity.move(Direction.RIGHT);

                if (fleeing.getPos().x != entity.getPos().x) avoidObstacles();
            } else succeed();
        }
    }

    @Override
    protected void reset() {
    }

    private void avoidObstacles() {

        if (entity.collidedX && entity.lastCollidedWith != fleeing) entity.jump();

        else if (fleeing.getPos().y <= entity.getPos().y) {

            boolean b = LevelMap.getInstance().getBlock((int) ((entity.getPos().x + entity.dim.width / 2 + entity.getVelocity().x) / Block.width),
                    (int) ((entity.getPos().y + entity.dim.height + 1) / Block.width), true).isOpaque();

            if (!b) entity.jump();
        }
    }
}
