package com.game.entities;

import com.util.Direction;
import com.util.Side;
import com.util.visual.AnimationInfo;

import java.awt.*;

/**
 * <code>EntityProjectile</code> thrown by the <code>EntityPlayer</code>.
 * Will travel in a straight line in the <code>Direction</code> the
 * <code>EntityPlayer</code> was facing.
 */
public class EntityGhostBall extends EntityProjectile {

    private final boolean big;

    /**
     * @param thrower the <code>Entity</code> this was thrown by. Will be ignored in collision checks.
     * @param big     whether this <code>EntityGhostBall</code> should be bigger, slower and deal more damage.
     */
    EntityGhostBall(Entity thrower, boolean big) {

        super(thrower, new Dimension(big ? 12 : 8, big ? 12 : 8), big ? 10 : 5);

        this.big = big;

        y += 5;

        maxVelocity = big ? 7 : 10;

        gravity = false;
    }

    @Override
    public void onUpdate() {

        if (big) setAnimationInUse(1);
        else setAnimationInUse(0);

        move(facing, 5);

        super.onUpdate();
    }

    @Override
    public void notifyCollision(Entity other, Side side) {

    }

    @Override
    protected void draw(Graphics g, int x, int y) {
        super.draw(g, x - (facing == Direction.LEFT ? 0 : dim.width), y);
    }

    @Override
    protected AnimationInfo[] createAnimationInfo() {
        AnimationInfo[] info = new AnimationInfo[2];
        info[0] = new AnimationInfo("GhostBall", 2, 5);
        info[1] = new AnimationInfo("GhostBall/Big", 10, 2);

        return info;
    }
}
