package com.game.entities;

import com.game.Level;
import com.game.Shield;
import com.util.visual.AnimationInfo;
import com.util.Side;

/**
 * Similar to the <code>EntityEnemy</code>, but cannot be hurt by projectiles
 * on the <code>Side</code> it is facing.
 */
public class EntityEnemyShield extends EntityEnemy {

    @SuppressWarnings("SameParameterValue")
    public EntityEnemyShield(int x, int y, Level level, int followRange) {
        super(x, y, level, followRange);

        shield = new Shield(this, 20, true, false);
    }

    @Override
    public void onUpdate() {

        super.onUpdate();

        if(shield.percentValue() <= 0) {
            level.removeEntity(this);
            EntityEnemy enemy = new EntityEnemy((int) x, (int) y, level, followRange);
            enemy.health = health;
            level.spawnEntity(enemy);
        }
    }

    @Override
    public void notifyCollision(Entity other, Side side) {

        if (other instanceof EntityProjectile)
            shield.damage(((EntityProjectile) other).damage, side == facing.toSide() || side == Side.TOP);
    }

    @Override
    protected AnimationInfo[] createAnimationInfo() {
        AnimationInfo[] info = new AnimationInfo[1];
        info[0] = new AnimationInfo("EnemyShield", 1, 10);

        return info;
    }
}
