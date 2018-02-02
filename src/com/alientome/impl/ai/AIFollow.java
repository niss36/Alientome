package com.alientome.impl.ai;

import com.alientome.core.util.Direction;
import com.alientome.game.ai.AI;
import com.alientome.game.entities.Entity;
import com.alientome.game.entities.EntityProjectile;

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
                double followingX = following.getPos().getX();
                double entityX = entity.getPos().getX();

                if (followingX > entityX) entity.move(Direction.RIGHT);
                else if (followingX < entityX) entity.move(Direction.LEFT);

                if (followingX != entityX) avoidObstacles();
            }
        }
    }

    @Override
    public void reset() {
    }

    private void avoidObstacles() {

        //TODO deal with duplicated code

        if (entity.collidedX && entity.lastCollidedWith != following && !(entity.lastCollidedWith instanceof EntityProjectile))
            entity.jump();

        else if (following.getPos().getY() <= entity.getPos().getY()) {

            boolean b = entity.level.getMap().getBlockAbsCoordinates(
                    entity.getPos().getX() + entity.dimension.width / 2 + entity.getVelocity().getX(),
                    entity.getPos().getY() + entity.dimension.height + 1).canBeCollidedWith();

            if (!b) entity.jump();
        }
    }
}
