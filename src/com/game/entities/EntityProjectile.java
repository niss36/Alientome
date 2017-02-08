package com.game.entities;

import com.game.blocks.Block;
import com.util.Vec2;
import com.util.collisions.Contact;

import java.awt.*;

/**
 * Used to represent an <code>Entity</code> who dies on collision and can damage
 * <code>Entity</code>s it collides with.
 */
public abstract class EntityProjectile extends Entity {

    final float damage;
    private final Entity thrower;

    /**
     * @param thrower   the <code>Entity</code> this was thrown by. Will be ignored in collision checks.
     * @param dimension the <code>Dimension</code> of this <code>Entity</code> (width, height)
     * @param damage    the amount to damage <code>Entity</code>s on collision
     */
    EntityProjectile(Entity thrower, Dimension dimension, float damage) {

        super(new Vec2(thrower.getPos().x + thrower.dimension.width / 2, thrower.getPos().y), dimension, thrower.level);

        this.thrower = thrower;

        this.damage = damage;

        facing = thrower.facing;
    }

    @Override
    public boolean onCollidedWithBlock(Block block, Contact contact) {
        if (super.onCollidedWithBlock(block, contact)) {

            setDead();

            return true;
        }

        return false;
    }

    @Override
    public boolean onCollidedWithEntity(Entity other, Contact contact) {

        if (other != thrower && !(other instanceof EntityProjectile)) {

            other.notifyCollision(this, contact);

            setDead();

            return true;
        }

        return false;
    }
}
