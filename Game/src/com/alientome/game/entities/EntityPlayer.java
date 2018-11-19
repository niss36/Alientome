package com.alientome.game.entities;

import com.alientome.core.vecmath.Vec2;
import com.alientome.game.entities.bars.FillColorProvider;
import com.alientome.game.level.Level;
import com.alientome.game.util.EntityTags;

import java.awt.*;

import static com.alientome.core.util.Colors.*;

public abstract class EntityPlayer extends EntityLiving {

    public EntityPlayer(Vec2 pos, Level level, EntityTags tags) {

        super(pos, new Dimension(20, 31), level, tags, 20);
    }

    @Override
    protected FillColorProvider getHealthFill() {
        return percentValue -> {
            if (percentValue >= 0.75) return HEALTH_75;
            if (percentValue >= 0.50) return HEALTH_50;
            return HEALTH_0;
        };
    }

    @Override
    protected void onSetDead() {
        super.onSetDead();
        controller.notifyControlledDeath();
    }

    @Override
    public String getNameKey() {
        return "entities.player.name";
    }
}
