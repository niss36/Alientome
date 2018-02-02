package com.alientome.impl.particles;

import com.alientome.core.vecmath.Vec2;
import com.alientome.game.level.Level;
import com.alientome.game.particles.ParticleSheet;

public class ParticleHeal extends ParticleSheet {

    public ParticleHeal(Vec2 pos, Level level, Vec2 velocity, int time) {
        super(pos, level, velocity, time);
    }
}
