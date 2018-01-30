package com.alientome.impl.entities;

import com.alientome.core.util.Direction;
import com.alientome.game.entities.Entity;
import com.alientome.game.entities.EntityProjectile;

import java.awt.*;

public class EntityArrow extends EntityProjectile {

    public EntityArrow(Entity thrower, float damage, double verticalMotion) {

        super(thrower, new Dimension(10, 3), damage);

        pos.y = Math.min(pos.y + 10 - verticalMotion, pos.y + thrower.dimension.height - 3);

        pos.x = facing == Direction.LEFT ? thrower.getPos().x : thrower.getPos().x + thrower.dimension.width;

        maxVelocity = 15;

        velocity.y = verticalMotion;

        velocity.x = facing == Direction.LEFT ? -maxVelocity : maxVelocity;
    }

    @Override
    protected void preUpdateInternal() {

        super.preUpdateInternal();

        move(facing, 0.25);
    }

    @Override
    public String getNameKey() {
        return "entities.arrow.name";
    }
}
