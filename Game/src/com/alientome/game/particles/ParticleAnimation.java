package com.alientome.game.particles;

import com.alientome.core.graphics.GameGraphics;
import com.alientome.core.vecmath.Vec2;
import com.alientome.game.SpritesLoader;
import com.alientome.game.level.Level;
import com.alientome.visual.animations.AnimationsHandler;

public abstract class ParticleAnimation extends Particle {

    protected final AnimationsHandler handler;

    protected ParticleAnimation(Vec2 pos, Level level, Vec2 velocity, int time) {
        super(level, pos, velocity, time);

        handler = SpritesLoader.newAnimationsHandlerFor(getClass());
    }

    @Override
    protected void draw(GameGraphics g, int x, int y) {
        handler.draw(g, x, y);
    }

    @Override
    protected boolean canDraw() {
        return handler.canDraw();
    }
}
