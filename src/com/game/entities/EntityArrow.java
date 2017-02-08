package com.game.entities;

import com.util.Direction;
import com.util.collisions.Contact;

import java.awt.*;

public class EntityArrow extends EntityProjectile {

    /**
     * @param thrower the <code>Entity</code> this was thrown by. Will be ignored in collision checks.
     * @param damage  the amount to damage <code>Entity</code>s on collision
     */
    EntityArrow(Entity thrower, float damage, double verticalMotion) {
        super(thrower, new Dimension(10, 3), damage);

        pos.y = Math.min(pos.y + 10 - verticalMotion, pos.y + thrower.dimension.height - 3);

        pos.x = facing == Direction.LEFT ? thrower.getPos().x : thrower.getPos().x + thrower.dimension.width;

        maxVelocity = 15;

        velocity.y = verticalMotion;

        velocity.x = facing == Direction.LEFT ? -maxVelocity : maxVelocity;
    }

    @Override
    void preUpdateInternal() {
        move(facing, 0.25);
    }

    @Override
    protected void notifyCollision(Entity other, Contact contact) {

    }
}
