package com.game.entities;

import com.game.entities.actions.Action;
import com.game.entities.ai.*;
import com.game.level.Level;
import com.util.Vec2;
import com.util.collisions.Contact;

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
     * @param pos this <code>Entity</code>'s position
     * @param level       the <code>Level</code> this <code>Entity</code> is in
     * @param followRange the range at which the <code>EntityEnemy</code>
     *                    will follow the <code>EntityPlayer</code>
     */
    EntityEnemy(Vec2 pos, Level level, int followRange) {
        this(pos, new Dimension(20, 27), level, 10, followRange, true);
    }

    /**
     * @param pos this <code>Entity</code>'s position
     * @param level        the <code>Level</code> this <code>Entity</code> is in
     * @param followRange  the range at which the <code>EntityEnemy</code>
     *                     will follow the <code>EntityPlayer</code>
     * @param damagePlayer whether this <code>EntityEnemy</code> should damage
     */
    EntityEnemy(Vec2 pos, Dimension dimension, Level level, int maxHealth, int followRange, boolean damagePlayer) {

        super(pos, dimension, level, maxHealth);

        maxVelocity = 3;

        this.followRange = (entityRandom.nextInt(50) + followRange - 25);
        actions = createActions();
        ai = createAI();
        controller = new AIController(ai);

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
    void preUpdateInternal() {
        /*theProfiler.startSection("Entity Update/AI Update");
        if (ai.getState() == null) ai.start();
        ai.act();
        theProfiler.endSection("Entity Update/AI Update");*/

        if (coolDown > 0) coolDown--;

        if (attackState >= 0) {

            if (attackState < 6) attackState++;
            else {
                attackState = -1;

                handler.setAnimationUsed(0);
            }
        }
    }

    AI createAI() {

        Entity player = level.getPlayer();

        AITest[] tests = {new AIEntityAbove(this, player, 10), new AIEntityAbove(this, player, 15), new AISeeEntity(this, player, followRange)};
        AI[] actions = {new AIFlee(this, player, 150), new AIIdle(this, -1), new AIFollow(this, player, false)};

        return new AIRepeat(
                new AITestAction(
                        tests,
                        actions,
                        new AIWander(this)
                )
        );
    }

    Action[] createActions() {
        return null;
    }

    void onCollidedWithPlayer(EntityPlayer player, Contact contact, double playerPreviousYVel) {
        if (contact.normal.y > 0) {
            damageAbsolute(player.getFallDamage(playerPreviousYVel) * 2);
            if (onGround) player.acceleration.y = playerPreviousYVel - 11;
        } else if (damagePlayer) {
            if (attackState == 5) player.damage(3);
            else attack();
        }
    }

    @Override
    protected void notifyCollision(Entity other, Contact contact) {
        super.notifyCollision(other, contact);

        if (other instanceof EntityPlayer)
            onCollidedWithPlayer((EntityPlayer) other, contact, other.velocity.y);
    }

    @Override
    public void onControlLost() {
        controller = new AIController(ai);
    }
}
