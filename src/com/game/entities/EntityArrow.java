package com.game.entities;

import com.util.Direction;
import com.util.Side;

import java.awt.*;

public class EntityArrow extends EntityProjectile {

    /**
     * @param thrower the <code>Entity</code> this was thrown by. Will be ignored in collision checks.
     * @param damage  the amount to damage <code>Entity</code>s on collision
     */
    EntityArrow(Entity thrower, int damage, double verticalMotion) {
        super(thrower, new Dimension(10, 3), damage);

        pos.y = Math.min(pos.y + 10 - verticalMotion, pos.y + thrower.dim.height - 3);

        pos.x = facing == Direction.LEFT ? thrower.getPos().x : thrower.getPos().x + thrower.dim.width;

        maxVelocity = 15;

        velocity.y = verticalMotion;

        velocity.x = facing == Direction.LEFT ? -maxVelocity : maxVelocity;
    }

    @Override
    public void onUpdate() {

        move(facing, 0.25);

        super.onUpdate();
    }

    @Override
    protected void notifyCollision(Entity other, Side side) {

    }
}
