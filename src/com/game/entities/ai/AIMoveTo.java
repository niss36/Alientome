package com.game.entities.ai;

import com.game.entities.Entity;
import com.util.Direction;

public class AIMoveTo extends AI {

    int destX;

    public AIMoveTo(Entity entity, int destX) {

        super(entity);

        this.destX = destX;
    }

    @Override
    public void reset() { }

    @Override
    public void act() {
        if(isRunning()) {
            if(entity.isDead()) fail();
            else move(entity);
        }
    }

    private void move(Entity entity) {

        if(destX != entity.getX()) {
            if(destX > entity.getX()) {
                entity.move(Direction.RIGHT);
            } else {
                entity.move(Direction.LEFT);
            }
        }
        if(isAtDestination(entity)) succeed();
    }

    private boolean isAtDestination(Entity entity) {

        return destX == entity.getX();
    }
}
