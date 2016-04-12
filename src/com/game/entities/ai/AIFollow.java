package com.game.entities.ai;

import com.game.entities.Entity;
import com.util.Direction;

/**
 * <code>AI</code> to make the target <code>Entity</code> follow an other <code>Entity</code>.
 * Fails if the target <code>Entity</code> fails, and is set to <code>State.FAIL</code>
 * or <code>State.SUCCESS</code> when the followed <code>Entity</code> dies according to
 * <code>this.onFollowedDeath</code>.
 */
public class AIFollow extends AI {

    private final Entity following;
    private final State onFollowedDeath;

    private final AISeeEntity aiSeeEntity;

    /**
     * @param entity the target <code>Entity</code>
     * @param following the <code>Entity</code> to follow
     * @param followRange the maximum following range
     * @param failOnFollowedDeath if this is <code>true</code>, the <code>AI</code>'s
     *                            <code>State</code> will be set to <code>State.FAIL</code> when the followed
     *                            <code>Entity</code> dies. Else it will be set to <code>State.SUCCESS</code>
     */
    public AIFollow(Entity entity, Entity following, int followRange, boolean failOnFollowedDeath) {
        super(entity);

        this.following = following;

        if (failOnFollowedDeath) onFollowedDeath = State.FAIL;
        else onFollowedDeath = State.SUCCESS;

        aiSeeEntity = new AISeeEntity(entity, following, followRange);
    }

    @Override
    public void act() {

        if (isRunning()) {

            if (entity.isDead()) fail();

            else if (following.isDead()) state = onFollowedDeath;

            else {
                aiSeeEntity.act();
                if(aiSeeEntity.isSuccess()) {

                    if(following.getX() > entity.getX()) entity.move(Direction.RIGHT);
                    else if(following.getX() < entity.getX()) entity.move(Direction.LEFT);

                } else if(aiSeeEntity.isFailure()) fail();
            }
        }
    }

    @Override
    public void reset() {
        start();
    }

    @Override
    public void start() {
        super.start();
        aiSeeEntity.start();
    }
}
