package com.alientome.game.entities;

import com.alientome.core.collisions.Contact;
import com.alientome.core.graphics.GameGraphics;
import com.alientome.core.util.Colors;
import com.alientome.core.vecmath.Constants;
import com.alientome.core.vecmath.Vec2;
import com.alientome.game.Shield;
import com.alientome.game.SpritesLoader;
import com.alientome.game.blocks.Block;
import com.alientome.game.entities.bars.FillColorProvider;
import com.alientome.game.entities.bars.SimpleFillColor;
import com.alientome.game.entities.bars.StatusBar;
import com.alientome.game.entities.bars.StatusValue;
import com.alientome.game.level.Level;
import com.alientome.game.util.EntityTags;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * Used to represent an <code>Entity</code> who has health and can be damaged and killed.
 */
public abstract class EntityLiving extends Entity implements StatusValue {

    private static final BufferedImage iconHealth = SpritesLoader.readImage("UI/iconHealth.png");
    private static final BufferedImage iconShield = SpritesLoader.readImage("UI/iconShield.png");

    protected final List<StatusBar> statusBars = new ArrayList<>();
    protected final int maxHealth;
    protected float health;
    protected Shield shield;

    private int damageCoolDown = 0;

    /**
     * @param pos       this <code>Entity</code>'s position
     * @param dimension the <code>Dimension</code> of this <code>Entity</code> (width, height)
     * @param level     the <code>Level</code> this <code>Entity</code> is in
     * @param defaultMaxHealth the maximum health this <code>EntityLiving</code> can have
     */
    protected EntityLiving(Vec2 pos, Dimension dimension, Level level, EntityTags tags, int defaultMaxHealth) {
        super(pos, dimension, level, tags);

        maxHealth = tags.getAsInt("maxHealth", defaultMaxHealth);
        health = Math.min(tags.getAsFloat("health", maxHealth), maxHealth);

        statusBars.add(new StatusBar(getHealthFill(), iconHealth, 6, true, this));
        statusBars.add(new StatusBar(new SimpleFillColor(Colors.SHIELD_BAR), iconShield, 6, true, () -> shield == null ? 0 : shield.percentValue()));
    }

    @Override
    protected void postUpdateInternal() {

        if (damageCoolDown > 0) damageCoolDown--;

        if (health <= 0) setDead();
    }

    @Override
    public boolean onCollidedWithBlock(Block block, Contact contact) {

        double tMotionY = velocity.getY();

        if (super.onCollidedWithBlock(block, contact)) {

            if (contact.normal == Constants.UNIT_MINUS_Y && tMotionY >= 15) {

                float damage = getFallDamage(tMotionY);
                damageAbsolute(damage);
            }

            return true;
        }

        return false;
    }

    @Override
    protected void notifyCollision(Entity other, Contact contact) {

        if (other instanceof EntityProjectile) damage(((EntityProjectile) other).damage);
    }

    protected abstract FillColorProvider getHealthFill();

    @Override
    protected void draw(GameGraphics g, int x, int y) {
        super.draw(g, x, y);

        drawStatusBars(g, x, y);
    }

    @Override
    protected void drawSpecial(GameGraphics g, int x, int y) {
        super.drawSpecial(g, x, y);

        drawStatusBars(g, x, y);
    }

    private void drawStatusBars(GameGraphics g, int x, int y) {

        if (isDead())
            return;

        x += dimension.width / 2;
        y -= 4;

        for (StatusBar bar : statusBars) {

            if (bar.willDraw(g.interpolation)) {
                y -= bar.getHeight();
                bar.draw(g, x, y);
            }
        }
    }

    /**
     * Removes <code>value</code> health to this <code>EntityLiving</code>'s health
     * if it was not already hurt recently.
     *
     * @param value the health to damage by
     */
    public void damage(float value) {
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
        health = Math.min(health + value, maxHealth);
    }

    public void addShield(float value) {

        shield = new Shield(this, value, false, true);
    }

    protected final float getFallDamage(double verticalMotion) {

        return verticalMotion >= 0 ? (float) verticalMotion / 5 : 0;
    }

    public float getHealth() {
        return health;
    }

    public void setHealth(float health) {
        this.health = health;
    }

    @Override
    public float percentValue() {
        return health / maxHealth;
    }
}
