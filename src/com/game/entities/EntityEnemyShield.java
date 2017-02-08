package com.game.entities;

import com.game.Shield;
import com.game.entities.ai.AIController;
import com.game.level.Level;
import com.util.Vec2;
import com.util.collisions.Contact;

/**
 * Similar to the <code>EntityEnemy</code>, but cannot be hurt by projectiles
 * on the <code>Direction</code> it is facing.
 */
public class EntityEnemyShield extends EntityEnemy {

    @SuppressWarnings("SameParameterValue")
    EntityEnemyShield(Vec2 pos, Level level, int followRange) {
        super(pos, level, followRange);

        shield = new Shield(this, 20, true, false);
    }

    @Override
    void postUpdateInternal() {

        if (shield.percentValue() <= 0) {
            level.removeEntity(this);
            EntityEnemy enemy = new EntityEnemy(pos, level, followRange);
            enemy.health = health;

            if (!(controller instanceof AIController)) {

                controller.copyControls(enemy.newController());
                enemy.controller = controller;
            }

            level.spawnEntity(enemy);
        }

        super.postUpdateInternal();
    }

    @Override
    protected void notifyCollision(Entity other, Contact contact) {

        if (other instanceof EntityProjectile)
            shield.damage(((EntityProjectile) other).damage, facing.normal() == contact.normal || contact.normal == Vec2.UNIT_MINUS_Y);

        else if (other instanceof EntityPlayer)
            onCollidedWithPlayer((EntityPlayer) other, contact, other.velocity.y);
    }
}
