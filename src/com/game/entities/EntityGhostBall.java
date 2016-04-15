package com.game.entities;

import com.util.Direction;
import com.util.Side;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * <code>EntityProjectile</code> thrown by the <code>EntityPlayer</code>.
 * Will travel in a straight line in the <code>Direction</code> the
 * <code>EntityPlayer</code> was facing.
 */
public class EntityGhostBall extends EntityProjectile {

    private static BufferedImage[] sprites;
    private static BufferedImage[] spritesBig;

    private final boolean big;

    /**
     * @param thrower the <code>Entity</code> this was thrown by. Will be ignored in collision checks.
     * @param big     whether this <code>EntityGhostBall</code> should be bigger, slower and deal more damage.
     */
    EntityGhostBall(Entity thrower, boolean big) {

        super(thrower, new Dimension(8 + (big ? 4 : 0), 8 + (big ? 4 : 0)), big ? 10 : 5);

        this.big = big;

        y += 5;

        maxVelocity = big ? 7 : 10;

        gravity = false;

        if (sprites == null) sprites = getSpritesAnimated("GhostBall", 2);
        if (spritesBig == null) spritesBig = getSpritesAnimated("GhostBall/Big", 10);
    }

    @Override
    public void onUpdate() {

        move(facing, 5);

        super.onUpdate();
    }

    @Override
    public void notifyCollision(Entity other, Side side) {

    }

    @Override
    public void draw(Graphics g, Point min, boolean debug) {

        int x = (int) this.x - min.x;
        int y = (int) this.y - min.y;

        if (big) drawAnimated(g, spritesBig, x + (facing == Direction.LEFT ? 0 : -12), y, 2);

        else drawAnimated(g, sprites, x + (facing == Direction.LEFT ? 0 : -8), y, 5);

        if (debug) drawBoundingBox(g, x, y);
    }
}
