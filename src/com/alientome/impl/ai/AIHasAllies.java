package com.alientome.impl.ai;

import com.alientome.game.entities.Entity;
import com.alientome.game.entities.EntityLiving;

public class AIHasAllies extends AITest {

    private final Class<? extends EntityLiving> alliesType;
    private final int minCount;
    private final double range;

    /**
     * @param entity the target <code>Entity</code>
     */
    public AIHasAllies(Entity entity, Class<? extends EntityLiving> alliesType, int minCount, double range) {
        super(entity);

        this.alliesType = alliesType;
        this.minCount = minCount;
        this.range = range;
    }

    @Override
    public void act() {

        if (entity.isDead()) fail();
        else if (entity.level.countObjectsInRange(alliesType, entity.getPos().x, entity.getPos().y, range, entity) >= minCount) {
//            System.out.println(">= " + minCount);
            succeed();
        } else fail();
    }

    @Override
    public void reset() {

    }
}
