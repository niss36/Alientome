package com.game.entities.ai;

import com.game.entities.Entity;

public class AIWander extends AI {

    private AIMoveTo aiMoveTo;
    private AIIdle aiIdle = new AIIdle(entity);

    public AIWander(Entity entity) {
        super(entity);
        reset();
    }

    @Override
    public void act() {

        if(aiMoveTo.isRunning()) {
            aiMoveTo.act();

            if(aiMoveTo.isSuccess() || entity.collidedX) aiIdle.start();
            else if(aiMoveTo.isFailure()) fail();
        } else if(aiIdle.isRunning()) {
            aiIdle.act();

            if(aiIdle.isSuccess()) succeed();
            else if(aiIdle.isFailure()) fail();
        }
    }

    @Override
    public void start() {
        super.start();
        aiMoveTo.start();
    }

    @Override
    public void reset() {
        aiMoveTo = new AIMoveTo(entity, (int) entity.getX() + aIRandom.nextInt(50) - 25);
        aiIdle.reset();
    }
}
