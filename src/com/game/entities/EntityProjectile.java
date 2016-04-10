package com.game.entities;

import com.game.Block;
import com.util.Side;

import java.awt.*;

public abstract class EntityProjectile extends Entity {

    protected final Entity thrower;

    EntityProjectile(Entity thrower, Dimension dim) {

        super(thrower.x + thrower.dim.width / 2, thrower.y, dim, thrower.level);

        this.thrower = thrower;

        facing = thrower.facing;
    }

    @Override
    public boolean onCollidedWithBlock(Block block, Side side) {
        if (super.onCollidedWithBlock(block, side)) {

            setDead();

            return true;
        }

        return false;
    }

    @Override
    public boolean onCollidedWithEntity(Entity other, Side side) {

        if (other != thrower && !(other instanceof EntityProjectile)) {

            /*if (other instanceof EntityLiving) ((EntityLiving) other).damage(5);*/

            other.notifyCollision(this, side);

            setDead();

            return true;
        }

        return false;
    }
}
