package com.alientome.game.entities;

import com.alientome.core.collisions.Contact;
import com.alientome.core.graphics.GameGraphics;
import com.alientome.core.vecmath.Vec2;
import com.alientome.game.blocks.Block;

import java.awt.*;

/**
 * Used to represent an Entity that behaves as a projectile, dying on collision and potentially damaging,
 * and knocking back, Entities it collides with.
 */
public abstract class EntityProjectile extends Entity {

    public final float damage;
    private final Entity thrower;

    /**
     * @param thrower   the <code>Entity</code> this was thrown by. Will be ignored in collision checks.
     * @param dimension the <code>Dimension</code> of this <code>Entity</code> (width, height).
     * @param damage    the amount to damage <code>Entity</code>s on collision.
     */
    protected EntityProjectile(Entity thrower, Dimension dimension, float damage) {

        super(new Vec2(thrower.getPos().getX() + thrower.dimension.width / 2, thrower.getPos().getY()), dimension, thrower.level, null);

        this.thrower = thrower;

        this.damage = damage;

        facing = thrower.facing;
    }

    @Override
    protected void collideBlock(Block block) {

        Contact contact = getNextBoundingBox().processContact(block.getBoundingBox());

        if (contact != null)
            onCollidedWithBlock(block, contact);
    }

    @Override
    public boolean beforeCollideWith(Entity other) {

        if (other instanceof EntityProjectile) {

            if (other.velocity.dotProduct(velocity) < 0) {

                setDead();
                other.setDead();
            }

            return false;
        }

        return other != thrower;
    }

    @Override
    public boolean onCollidedWithBlock(Block block, Contact contact) {

        boolean collided = super.onCollidedWithBlock(block, contact);

        if (collided)
            setDead();

        return collided;
    }

    @Override
    protected void draw(GameGraphics g, int x, int y) {
        if (!dead)
            super.draw(g, x, y);
    }

    @Override
    protected void notifyCollision(Entity other, Contact contact) {

        other.velocity.add(
                -knockbackXFactor() * contact.normal.getX(),
                -knockbackYFactor());

        setDead();
    }

    protected double knockbackXFactor() {
        return 3;
    }

    protected double knockbackYFactor() {
        return 0;
    }
}
