package com.game.entities.ai;

import com.game.entities.Entity;
import com.util.Direction;

public class AIFollow extends AI {

    Entity following;
    int followRange;
    State onFollowedDeath;

    AISeeEntity aiSeeEntity;

    public AIFollow(Entity entity, Entity following, int followRange, boolean failOnFollowedDeath) {
        super(entity);

        this.following = following;
        this.followRange = followRange;

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

                } else if(aiSeeEntity.isFailure()) aiSeeEntity.reset();
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
