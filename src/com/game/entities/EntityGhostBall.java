package com.game.entities;

import com.util.Direction;
import com.util.Side;

import java.awt.*;

/**
 * <code>EntityProjectile</code> thrown by the <code>EntityPlayer</code>.
 * Will travel in a straight line in the <code>Direction</code> the
 * <code>EntityPlayer</code> was facing.
 */
public class EntityGhostBall extends EntityProjectile {

    /**
     * @param thrower the <code>Entity</code> this was thrown by. Will be ignored in collision checks.
     * @param big     whether this <code>EntityGhostBall</code> should be bigger, slower and deal more damage.
     */
    EntityGhostBall(Entity thrower, boolean big) {

        super(thrower, new Dimension(big ? 12 : 8, big ? 12 : 8), big ? 10 : 5);

        pos.y += 5;

        maxVelocity = big ? 7 : 10;

        if (big) handler.setAnimationUsed(1);
        else handler.setAnimationUsed(0);
    }

    @Override
    public void onUpdate() {

        move(facing, 5);

        super.onUpdate();
    }

    @Override
    protected void draw(Graphics g, int x, int y) {
        super.draw(g, x - (facing == Direction.LEFT ? 0 : dim.width), y);
    }

    @Override
    boolean isAffectedByGravity() {
        return false;
    }

    @Override
    public void notifyCollision(Entity other, Side side) {

    }
}
