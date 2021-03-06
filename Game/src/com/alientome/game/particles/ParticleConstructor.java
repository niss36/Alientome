package com.alientome.game.particles;

import com.alientome.core.util.Vec2;

@FunctionalInterface
public interface ParticleConstructor {

    Particle create(Vec2 pos, Vec2 velocity, int time);
}
