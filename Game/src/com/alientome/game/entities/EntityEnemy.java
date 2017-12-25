package com.alientome.game.entities;

import com.alientome.core.collisions.Contact;
import com.alientome.core.util.Colors;
import com.alientome.core.util.Vec2;
import com.alientome.game.ai.AI;
import com.alientome.game.ai.AIController;
import com.alientome.game.entities.bars.FillColorProvider;
import com.alientome.game.entities.bars.SimpleFillColor;
import com.alientome.game.level.Level;
import com.alientome.game.util.EntityTags;

import java.awt.*;

public abstract class EntityEnemy extends EntityLiving {

    private final AI ai;

    protected EntityEnemy(Vec2 pos, Dimension dimension, Level level, EntityTags tags, int defaultMaxHealth) {
        super(pos, dimension, level, tags, defaultMaxHealth);

        ai = createAI(tags);
        controller = new AIController(ai);
    }

    @Override
    protected FillColorProvider getHealthFill() {
        return new SimpleFillColor(Colors.HEALTH_ENEMY);
    }

    protected abstract AI createAI(EntityTags tags);

    protected boolean onCollidedWithPlayer(EntityPlayer player, Contact contact) {

        if (contact.normal.y > 0) {

            damageAbsolute(player.getFallDamage(player.velocity.y) * 2);
            if (onGround) player.velocity.y -= 11;

            return true;
        }

        return false;
    }

    @Override
    protected void notifyCollision(Entity other, Contact contact) {
        super.notifyCollision(other, contact);

        if (other instanceof EntityPlayer)
            onCollidedWithPlayer((EntityPlayer) other, contact);
    }

    @Override
    public void onControlLost() {
        controller = new AIController(ai);
        controller.reset();
    }
}
