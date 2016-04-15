package com.game.entities;

import com.game.level.Level;
import com.util.Side;

import java.awt.*;

/**
 * Similar to the <code>EntityEnemy/code>, but cannot be hurt by projectiles
 * on the <code>Side</code> it is facing.
 */
public class EntityEnemyShield extends EntityEnemy {

    public EntityEnemyShield(int x, int y, Level level, int followRange) {
        super(x, y, level, followRange);
    }

    @Override
    public void notifyCollision(Entity other, Side side) {

        if (other instanceof EntityProjectile && side != facing.toSide()) damage(5);

    }

    @Override
    public void draw(Graphics g, Point min, boolean debug) {

        g.setColor(Color.yellow);

        super.draw(g, min, debug);
    }
}
