package com.alientome.game.particles;

import com.alientome.core.util.Vec2;
import com.alientome.game.level.Level;

public class ParticlePlayerCharge extends ParticleSheet {

    public ParticlePlayerCharge(Vec2 pos, Level level, Vec2 velocity, int time, int variant) {
        super(pos, level, velocity, time);

        handler.setVariantUsed(0, variant);
    }
}
