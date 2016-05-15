package com.game.entities;

import com.game.Level;
import com.game.entities.ai.*;
import com.util.visual.AnimationInfo;
import com.util.Side;

import java.awt.*;

/**
 * This <code>Entity</code> is hostile towards the <code>EntityPlayer</code>
 * and will try to kill him.
 */
public class EntityEnemy extends EntityLiving {

    private final boolean damagePlayer;
    final int followRange;
    private final AI ai;

    /**
     * @param x           the x coordinate
     * @param y           the y coordinate
     * @param level       the <code>Level</code> this <code>Entity</code> is in
     * @param followRange the range at which the <code>EntityEnemy</code>
     *                    will follow the <code>EntityPlayer</code>
     */
    public EntityEnemy(int x, int y, Level level, int followRange) {
        this(x, y, level, new Dimension(20, 27), followRange, true);
    }

    /**
     * @param x            the x coordinate
     * @param y            the y coordinate
     * @param level        the <code>Level</code> this <code>Entity</code> is in
     * @param followRange  the range at which the <code>EntityEnemy</code>
     *                     will follow the <code>EntityPlayer</code>
     * @param damagePlayer whether this <code>EntityEnemy</code> should damage
     *                     the <code>EntityPlayer</code> on contact
     */
    EntityEnemy(int x, int y, Level level, Dimension dim, int followRange, boolean damagePlayer) {

        super(x, y, dim, level, 10);

        maxVelocity = 3;

        this.followRange = entityRandom.nextInt(50) + followRange - 25;

        ai = createAI();

        this.damagePlayer = damagePlayer;

    }

    @Override
    public void onUpdate() {

        if (ai.getState() == null) ai.start();
        ai.act();

        super.onUpdate();
    }

    @Override
    public boolean onCollidedWithEntity(Entity other, Side side) {

        double tMotionY = other.motionY;

        if (super.onCollidedWithEntity(other, side)) {

            if (other instanceof EntityPlayer) {
                if (side == Side.BOTTOM) {
                    damage(((EntityPlayer) other).getFallDamage(tMotionY) * 2);
                    if (onGround) other.motionY = tMotionY - 11;
                } else if (damagePlayer) ((EntityPlayer) other).damage(1);
            }

            return true;
        }

        return false;
    }

    @Override
    protected AnimationInfo[] createAnimationInfo() {
        AnimationInfo[] info = new AnimationInfo[1];
        info[0] = new AnimationInfo("Enemy", 1, 10);

        return info;
    }

    AI createAI() {

        AITest[] tests = {new AIEntityAbove(this, level.player), new AISeeEntity(this, level.player, followRange)};
        AI[] actions = {new AIFlee(this, level.player, 150), new AIFollow(this, level.player, false)};

        return new AIRepeat(
                new AITestAction(
                        tests,
                        actions,
                        new AIWander(this)
                )
        );
    }
}
