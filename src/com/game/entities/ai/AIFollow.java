package com.game.entities.ai;

import com.game.blocks.Block;
import com.game.entities.Entity;
import com.game.level.LevelMap;
import com.util.Direction;

/**
 * <code>AI</code> to make the target <code>Entity</code> follow an other <code>Entity</code>.
 * Fails if the target <code>Entity</code> dies, and is set to <code>State.FAIL</code>
 * or <code>State.SUCCESS</code> when the followed <code>Entity</code> dies according to
 * <code>this.onFollowedDeath</code>.
 */
public class AIFollow extends AI {

    private final Entity following;
    private final State onFollowedDeath;

    /**
     * @param entity              the target <code>Entity</code>
     * @param following           the <code>Entity</code> to follow
     * @param failOnFollowedDeath if this is <code>true</code>, the <code>AI</code>'s
     *                            <code>State</code> will be set to <code>State.FAIL</code> when the followed
     *                            <code>Entity</code> dies. Else it will be set to <code>State.SUCCESS</code>
     */
    public AIFollow(Entity entity, Entity following, boolean failOnFollowedDeath) {
        super(entity);

        this.following = following;

        if (failOnFollowedDeath) onFollowedDeath = State.FAIL;
        else onFollowedDeath = State.SUCCESS;
    }

    @Override
    public void act() {

        if (isRunning()) {

            if (entity.isDead()) fail();

            else if (following.isDead()) state = onFollowedDeath;

            else {
                if (following.getPos().x > entity.getPos().x) entity.move(Direction.RIGHT);
                else if (following.getPos().x < entity.getPos().x) entity.move(Direction.LEFT);

                if (following.getPos().x != entity.getPos().x) avoidObstacles();
            }
        }
    }

    @Override
    protected void reset() {
    }

    private void avoidObstacles() {

        if (entity.collidedX && entity.lastCollidedWith != following) entity.jump();

        else if (following.getPos().y <= entity.getPos().y) {

            boolean b = LevelMap.getInstance().getBlock((int) ((entity.getPos().x + entity.dim.width / 2 + entity.getVelocity().x) / Block.width),
                    (int) ((entity.getPos().y + entity.dim.height + 1) / Block.width), true).isOpaque();

            if (!b) entity.jump();
        }
    }
}
