package com.game.entities;

import com.game.blocks.Block;
import com.util.CollisionPoint;
import com.util.Side;

import java.awt.*;

/**
 * Used to represent an <code>Entity</code> who dies on collision and can damage
 * <code>Entity</code>s it collides with.
 */
public abstract class EntityProjectile extends Entity {

    final int damage;
    private final Entity thrower;

    /**
     * @param thrower the <code>Entity</code> this was thrown by. Will be ignored in collision checks.
     * @param dim     the <code>Dimension</code> of this <code>Entity</code> (width, height)
     * @param damage  the amount to damage <code>Entity</code>s on collision
     */
    EntityProjectile(Entity thrower, Dimension dim, int damage) {

        super(thrower.getPos().x + thrower.dim.width / 2, thrower.getPos().y, dim, thrower.level);

        this.thrower = thrower;

        this.damage = damage;

        facing = thrower.facing;
    }

    @Override
    public boolean onCollidedWithBlock(Block block, CollisionPoint collisionPoint) {
        if (super.onCollidedWithBlock(block, collisionPoint)) {

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
