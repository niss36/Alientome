package com.game.entities;

import com.game.entities.ai.*;
import com.game.level.Level;
import com.util.Side;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * This <code>Entity</code> is hostile towards the <code>EntityPlayer</code>
 * and will try to kill him.
 */
public class EntityEnemy extends EntityLiving {

    private static BufferedImage[] sprites;

    private final int followRange;
    private final AI ai;
    //private boolean stuck;

    /**
     * @param x           the x coordinate
     * @param y           the y coordinate
     * @param level       the <code>Level</code> this <code>Entity</code> is in
     * @param followRange the range at which the <code>EntityEnemy</code>
     *                    will follow the <code>EntityPlayer</code>
     */
    public EntityEnemy(int x, int y, Level level, int followRange) {

        super(x, y, new Dimension(20, 27), level, 10);

        maxVelocity = 3;

        this.followRange = entityRandom.nextInt(50) + followRange - 25;

        ai = new AIRepeat(
                new AISelector(
                        new AIFollow(this, level.player, this.followRange, false),
                        new AIWander(this)
                )
        );

        if (sprites == null) sprites = getSpritesAnimated("Enemy", 1);
    }

    @Override
    public void onUpdate() {

        if (ai.getState() == null) ai.start();
        ai.act();

        super.onUpdate();

        /*double xDif = x - level.player.x;
        double yDif = y - level.player.y;

        if (Math.sqrt(xDif * xDif + yDif * yDif) <= followRange) {

            if (level.player.x < x) move(Direction.LEFT);
            if (level.player.x > x) move(Direction.RIGHT);

            if (collidedX && level.player.x != x) {
                if (!stuck) jump();
                else
                    stuck = true;
            }

            boolean b = false;

            for (int i = 0; i < 2; i++)
                b = new Block((int) ((x + motionX + i * dim.width - i) / blockWidth), (int) ((y + 1 + dim.height) / blockWidth)).isOpaque() || b;

            if (!b && level.player.y <= y) jump();
        }*/
    }

    @Override
    public boolean onCollidedWithEntity(Entity other, Side side) {
        if (super.onCollidedWithEntity(other, side)) {

            if (other instanceof EntityPlayer) {
                if (side == Side.BOTTOM) {
                    damage(10);
                    if (onGround) other.motionY = -11;
                } else ((EntityPlayer) other).damage(1);
            }

            return true;
        }

        return false;
    }

    @Override
    public void draw(Graphics g, Point min, boolean debug) {

        int x = (int) this.x - min.x;
        int y = (int) this.y - min.y;

        super.drawAnimated(g, sprites, x, y, 10);

        if (debug) drawBoundingBox(g, x, y);

        g.setColor(Color.orange);

        drawHealthBar(g, min);
    }
}
