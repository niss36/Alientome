package com.game.entities.ai;

import com.game.entities.Entity;

public class AISeeEntity extends AI {

    Entity lookingFor;
    int range;

    public AISeeEntity(Entity entity, Entity lookingFor, int range) {
        super(entity);

        this.lookingFor = lookingFor;
        this.range = range;
    }

    @Override
    public void act() {
        if (entity.isDead()) fail();
        else if (lookingFor.isDead()) fail();

        else if (entity.distanceTo(lookingFor) > range) fail();

        else if (entity.level.canSeeEntity(entity, lookingFor)) succeed();
        else fail();
    }

    @Override
    public void reset() {
        start();
    }
}
