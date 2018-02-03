package com.alientome.impl.entities;

import com.alientome.core.util.Direction;
import com.alientome.game.entities.Entity;
import com.alientome.game.entities.EntityProjectile;

import java.awt.*;

public class EntityArrow extends EntityProjectile {

    public EntityArrow(Entity thrower, float damage, double verticalMotion) {

        super(thrower, new Dimension(10, 3), damage);

        pos.setY(Math.min(pos.getY() + 10 - verticalMotion, pos.getY() + thrower.dimension.height - 3));

        pos.setX(facing == Direction.LEFT ? thrower.getPos().getX() : thrower.getPos().getX() + thrower.dimension.width);

        maxVelocity = 15;

        velocity.setY(verticalMotion);

        velocity.setX(facing == Direction.LEFT ? -maxVelocity : maxVelocity);
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
