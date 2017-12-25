package com.alientome.impl.ai;

import com.alientome.game.entities.EntityLiving;

public class AIHealth extends AITest {

    private final float healthThreshold;
    private final boolean type;

    public AIHealth(EntityLiving entity, float healthThreshold, boolean type) {
        super(entity);

        this.healthThreshold = healthThreshold;
        this.type = type;
    }

    @Override
    public void act() {

        EntityLiving living = (EntityLiving) entity;

        if (living.isDead()) fail();

        else if (living.getHealth() <= healthThreshold)
            if (type) fail();
            else succeed();

        else if (living.getHealth() >= healthThreshold)
            if (type) succeed();
            else fail();
    }

    @Override
    public void reset() {

    }
}
