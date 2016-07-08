package com.game.entities;

import com.game.Shield;
import com.game.level.Level;
import com.util.Side;

/**
 * Similar to the <code>EntityEnemy</code>, but cannot be hurt by projectiles
 * on the <code>Side</code> it is facing.
 */
public class EntityEnemyShield extends EntityEnemy {

    @SuppressWarnings("SameParameterValue")
    EntityEnemyShield(double x, double y, Level level, int followRange) {
        super(x, y, level, followRange);

        shield = new Shield(this, 20, true, false);
    }

    @Override
    public void onUpdate() {

        super.onUpdate();

        if (shield.percentValue() <= 0) {
            level.removeEntity(this);
            EntityEnemy enemy = new EntityEnemy(pos.x, pos.y, level, followRange);
            enemy.health = health;
            level.spawnEntity(enemy);
        }
    }

    @Override
    protected void notifyCollision(Entity other, Side side) {

        if (other instanceof EntityProjectile)
            shield.damage(((EntityProjectile) other).damage, side == facing.toSide() || side == Side.TOP);
    }
}
