package com.game.entities;

import com.util.Direction;
import com.util.Side;
import com.util.visual.AnimationInfo;

import java.awt.*;

public class EntityArrow extends EntityProjectile {

    /**
     * @param thrower the <code>Entity</code> this was thrown by. Will be ignored in collision checks.
     * @param damage  the amount to damage <code>Entity</code>s on collision
     */
    EntityArrow(Entity thrower, int damage, double verticalMotion) {
        super(thrower, new Dimension(10, 3), damage);

        y = Math.min(y + 10 - verticalMotion, y + thrower.dim.height - 3);

        x = facing == Direction.LEFT ? thrower.x : thrower.x + thrower.dim.width;

        maxVelocity = 15;

        motionY = verticalMotion;

        motionX = facing == Direction.LEFT ? -maxVelocity : maxVelocity;
    }

    @Override
    public void onUpdate() {

        move(facing, 0.25);

        super.onUpdate();
    }

    @Override
    protected void notifyCollision(Entity other, Side side) {

    }

    @Override
    protected AnimationInfo[] createAnimationInfo() {
        AnimationInfo[] info = new AnimationInfo[1];
        info[0] = new AnimationInfo("Arrow", 1, 10);

        return info;
    }
}
