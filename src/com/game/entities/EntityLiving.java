package com.game.entities;

import com.game.Block;
import com.game.level.Level;
import com.util.Side;

import java.awt.*;

/**
 * Used to represent an <code>Entity</code> who has health and can be killed.
 */
public abstract class EntityLiving extends Entity {

    private final int maxHealth;
    private int health = 0;

    private int damageCoolDown = 0;

    /**
     * @param x         the x coordinate
     * @param y         the y coordinate
     * @param dim       the <code>Dimension</code> of this <code>Entity</code> (width, height)
     * @param level     the <code>Level</code> this <code>Entity</code> is in
     * @param maxHealth the maximum health this <code>EntityLiving</code> can have
     */
    EntityLiving(int x, int y, Dimension dim, Level level, int maxHealth) {
        super(x, y, dim, level);

        this.maxHealth = health = maxHealth;
    }

    @Override
    public void onUpdate() {

        super.onUpdate();

        if (damageCoolDown > 0) damageCoolDown--;

        if (health <= 0) setDead();

        if (blockIn.getIndex() == 2) damage(1);
    }

    @Override
    public void draw(Graphics g, Point min, boolean debug) {
        super.draw(g, min, debug);
        drawHealthBar(g, min);
    }

    /**
     * Used to visually represent this <code>EntityLiving</code>'s health.
     *
     * @param g   the <code>Graphics</code> to draw with
     * @param min the relative origin
     */
    void drawHealthBar(Graphics g, Point min) {

        if (health <= 0) return;

        double x = this.x - min.x;
        double y = this.y - min.y;

        g.fillRect((int) x - 5, (int) y - 10, 30, 6);

        float percentHP = (float) health / maxHealth;

        if (this instanceof EntityEnemy) g.setColor(Color.red);
        else if (percentHP >= 0.75) g.setColor(Color.green);
        else if (percentHP >= 0.5) g.setColor(Color.yellow);
        else if (percentHP >= 0) g.setColor(Color.red);

        g.fillRect((int) x - 4, (int) y - 9, (int) (percentHP * 28), 4);
    }

    @Override
    public boolean onCollidedWithBlock(Block block, Side side) {

        return super.onCollidedWithBlock(block, side);

        //if(block.getIndex() == 2) damage(5);
    }

    @Override
    public void notifyCollision(Entity other, Side side) {

        if (other instanceof EntityProjectile) damage(((EntityProjectile) other).damage);
    }

    /**
     * Removes <code>value</code> health to this <code>EntityLiving</code>'s health
     * if it was not already hurt recently.
     *
     * @param value the health to damage by
     */
    public void damage(int value) {
        if (damageCoolDown == 0) {
            health -= value;
            damageCoolDown = 1;
        }
    }
}
