package com.game.entities;

import com.game.entities.actions.Action;
import com.game.entities.ai.*;
import com.game.level.Level;
import com.util.Side;

import java.awt.*;

/**
 * This <code>Entity</code> is hostile towards the <code>EntityPlayer</code>
 * and will try to kill him.
 */
public class EntityEnemy extends EntityLiving {

    final int followRange;
    final Action[] actions;
    private final boolean damagePlayer;
    private final AI ai;
    private int coolDown = 0;
    private int attackState = -1;

    /**
     * @param x           the x coordinate
     * @param y           the y coordinate
     * @param level       the <code>Level</code> this <code>Entity</code> is in
     * @param followRange the range at which the <code>EntityEnemy</code>
     *                    will follow the <code>EntityPlayer</code>
     */
    EntityEnemy(double x, double y, Level level, int followRange) {
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
    EntityEnemy(double x, double y, Level level, Dimension dim, int followRange, boolean damagePlayer) {

        super(x, y, dim, level, 10);

        maxVelocity = 3;

        this.followRange = (entityRandom.nextInt(50) + followRange - 25)/* * 2*/;
        actions = createActions();
        ai = createAI();

        this.damagePlayer = damagePlayer;

    }

    private void attack() {
        if (coolDown == 0) {
            attackState = 0;

            handler.setAnimationUsed(1);

            coolDown = 7;
        }
    }

    @Override
    public void onUpdate() {

        if (ai.getState() == null) ai.start();
        ai.act();

        if (coolDown > 0) coolDown--;

        if (attackState >= 0) {

            if (attackState < 6) attackState++;
            else {
                attackState = -1;

                handler.setAnimationUsed(0);
            }
        }

        super.onUpdate();
    }

    @Override
    public boolean onCollidedWithEntity(Entity other, Side side) {

        double tMotionY = other.velocity.y;

        if (super.onCollidedWithEntity(other, side)) {

            if (other instanceof EntityPlayer) {
                if (side == Side.BOTTOM) {
                    damageAbsolute(((EntityPlayer) other).getFallDamage(tMotionY) * 2);
                    if (onGround) other.velocity.y = tMotionY - 11;
                } else if (damagePlayer) {
                    if (attackState == 5) ((EntityPlayer) other).damage(3);
                    else attack();
                }
            }

            return true;
        }

        return false;
    }

    AI createAI() {

        AITest[] tests = {new AIEntityAbove(this, level.player, 10/* * 2*/), new AIEntityAbove(this, level.player, 15/* * 2*/), new AISeeEntity(this, level.player, followRange)};
        AI[] actions = {new AIFlee(this, level.player, 150/* * 2*/), new AIIdle(this, -1), new AIFollow(this, level.player, false)};

        return new AIRepeat(
                new AITestAction(
                        tests,
                        actions,
                        new AIWander(this)
                )
        );
    }

    Action[] createActions() {
        return new Action[0];
    }
}
