package com.game.entities;

import com.game.Level;
import com.game.entities.ai.*;
import com.util.Side;
import com.util.SpritesLoader;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * This <code>Entity</code> is hostile towards the <code>EntityPlayer</code>
 * and will try to kill him.
 */
public class EntityEnemy extends EntityLiving {

    private static BufferedImage[] sprites;

    private final AI ai;

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

        followRange = entityRandom.nextInt(50) + followRange - 25;

        AITest[] tests = {new AIEntityAbove(this, level.player), new AISeeEntity(this, level.player, followRange)};
        AI[] actions = {new AIFlee(this, level.player, 150), new AIFollow(this, level.player, false)};

        ai = new AIRepeat(
                new AITestAction(
                        tests,
                        actions,
                        new AIWander(this)
                )
        );

        if (sprites == null) sprites = SpritesLoader.getSpritesAnimated("Enemy", 1);
    }

    @Override
    public void onUpdate() {

        if (ai.getState() == null) ai.start();
        ai.act();

        super.onUpdate();
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

        g.setColor(Color.black);

        drawHealthBar(g, min);
    }
}
