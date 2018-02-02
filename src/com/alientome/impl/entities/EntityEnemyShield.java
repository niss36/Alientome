package com.alientome.impl.entities;

import com.alientome.core.collisions.Contact;
import com.alientome.core.util.Vec2;
import com.alientome.game.Shield;
import com.alientome.game.ai.AIController;
import com.alientome.game.entities.Entity;
import com.alientome.game.entities.EntityEnemy;
import com.alientome.game.entities.EntityProjectile;
import com.alientome.game.level.Level;
import com.alientome.game.util.EntityTags;

import java.util.LinkedHashMap;
import java.util.Map;

public class EntityEnemyShield extends EntityEnemyDefault {

    public EntityEnemyShield(Vec2 pos, Level level, EntityTags tags) {

        super(pos, level, tags);

        shield = new Shield(this, 20, true, false);
    }

    @Override
    protected void postUpdateInternal() {

        if (shield.percentValue() <= 0) {
            level.removeEntity(this);
            Map<String, String> tags = new LinkedHashMap<>();
            tags.put("orientation", facing.toString());
            tags.put("maxHealth", String.valueOf(maxHealth));
            tags.put("health", String.valueOf(health));
            tags.put("followRange", String.valueOf(followRange));
            EntityEnemy enemy = new EntityEnemyDefault(pos, level, new EntityTags(tags));

            if (!(controller instanceof AIController)) {

                controller.copyControls(enemy.newController());
                enemy.setController(controller);
            }

            level.spawnEntity(enemy);
        }

        super.postUpdateInternal();
    }

    @Override
    protected void notifyCollision(Entity other, Contact contact) {

        if (other instanceof EntityProjectile)
            shield.damage(((EntityProjectile) other).damage, facing.normal.negateImmutable().equals(contact.normal));
        else
            super.notifyCollision(other, contact);
    }

    @Override
    public String getNameKey() {
        return "entities.enemyShield.name";
    }
}
