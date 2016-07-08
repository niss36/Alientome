package com.game.entities;

import com.game.Shield;
import com.game.blocks.Block;
import com.game.level.Level;
import com.util.CollisionPoint;
import com.util.Side;
import com.util.visual.SpritesLoader;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Used to represent an <code>Entity</code> who has health and can be killed.
 */
public abstract class EntityLiving extends Entity {

    private static BufferedImage iconHealth;
    private static BufferedImage iconShield;

    private final int maxHealth;
    float health = 0;

    Shield shield;

    private int damageCoolDown = 0;

    /**
     * @param x         the x coordinate
     * @param y         the y coordinate
     * @param dim       the <code>Dimension</code> of this <code>Entity</code> (width, height)
     * @param level     the <code>Level</code> this <code>Entity</code> is in
     * @param maxHealth the maximum health this <code>EntityLiving</code> can have
     */
    EntityLiving(double x, double y, Dimension dim, Level level, int maxHealth) {
        super(x, y, dim, level);

        health = this.maxHealth = maxHealth;

        if (iconHealth == null) iconHealth = SpritesLoader.getSprite("Misc/iconHealth");
        if (iconShield == null) iconShield = SpritesLoader.getSprite("Misc/iconShield");
    }

    @Override
    public void onUpdate() {

        super.onUpdate();

        if (damageCoolDown > 0) damageCoolDown--;

        if (health <= 0) setDead();

        if (blockIn.getIndex() == Block.HOLE) damage(1);
    }

    @Override
    public boolean onCollidedWithBlock(Block block, CollisionPoint collisionPoint) {

        double tMotionY = velocity.y;

        if (super.onCollidedWithBlock(block, collisionPoint)) {

            if (collisionPoint.getCollisionSide() == Side.TOP && tMotionY >= 15) {

                float damage = getFallDamage(tMotionY);
                damageAbsolute(damage);

            }

            return true;
        }

        return false;
    }

    @Override
    protected void notifyCollision(Entity other, Side side) {

        if (other instanceof EntityProjectile) damage(((EntityProjectile) other).damage);
    }

    @Override
    public void draw(Graphics g, Point min, boolean debug) {
        super.draw(g, min, debug);
        drawStatusBars(g, min);
    }

    private void drawStatusBars(Graphics g, Point min) {

        double x = pos.x - min.x;
        double y = pos.y - min.y;

        drawHealthBar(g, x, y);
        drawShieldBar(g, x, y - 6);
    }

    /**
     * Used to visually represent this <code>EntityLiving</code>'s health.
     *
     * @param g the <code>Graphics</code> to draw with
     */
    private void drawHealthBar(Graphics g, double x, double y) {

        if (health <= 0) return;

        g.setColor(Color.black);
        g.fillRect((int) x - 5, (int) y - 10, 30, 6);

        g.drawImage(iconHealth, (int) x - 11, (int) y - 10, null);

        float percentHP = health / maxHealth;

        if (this instanceof EntityEnemy) g.setColor(Color.red);
        else if (percentHP >= 0.75) g.setColor(Color.green);
        else if (percentHP >= 0.5) g.setColor(Color.yellow);
        else if (percentHP >= 0) g.setColor(Color.red);

        g.fillRect((int) x - 4, (int) y - 9, (int) (percentHP * 28), 4);
    }

    private void drawShieldBar(Graphics g, double x, double y) {

        if (shield == null || shield.percentValue() <= 0) return;

        g.setColor(Color.black);
        g.fillRect((int) x - 5, (int) y - 10, 30, 6);

        g.drawImage(iconShield, (int) x - 11, (int) y - 10, null);

        g.setColor(Color.blue);
        g.fillRect((int) x - 4, (int) y - 9, (int) (shield.percentValue() * 28), 4);
    }

    /**
     * Removes <code>value</code> health to this <code>EntityLiving</code>'s health
     * if it was not already hurt recently.
     *
     * @param value the health to damage by
     */
    void damage(float value) {
        if (damageCoolDown == 0) {
            if (shield == null) damageAbsolute(value);
            else shield.damage(value, false);
            damageCoolDown = 1;
        }
    }

    public void damageAbsolute(float value) {
        if (damageCoolDown == 0) {
            health -= value;
            damageCoolDown = 1;
        }
    }

    public void heal(float value) {
        health += value;
        if (health > maxHealth) health = maxHealth;
    }

    public void addShield(float value) {

        shield = new Shield(this, value, false, true);
    }

    final float getFallDamage(double verticalMotion) {

        return verticalMotion >= 0 ? (float) verticalMotion / 5 : 0;
    }
}
