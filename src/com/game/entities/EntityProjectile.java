package com.game.entities;

import com.game.Block;
import com.util.Side;

import java.awt.*;

/**
 * Used to represent an <code>Entity</code> who dies on collision and can damage
 * <code>Entity</code>s it collides with.
 */
public abstract class EntityProjectile extends Entity {

    private final Entity thrower;

    final int damage;

    /**
     * @param thrower the <code>Entity</code> this was thrown by. Will be ignored in collision checks.
     * @param dim     the <code>Dimension</code> of this <code>Entity</code> (width, height)
     * @param damage  the amount to damage <code>Entity</code>s on collision
     */
    EntityProjectile(Entity thrower, Dimension dim, int damage) {

        super(thrower.x + thrower.dim.width / 2, thrower.y, dim, thrower.level);

        this.thrower = thrower;

        this.damage = damage;

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

            other.notifyCollision(this, side);

            setDead();

            return true;
        }

        return false;
    }
}
