package com.alientome.game.particles;

import com.alientome.core.collisions.AxisAlignedBoundingBox;
import com.alientome.core.graphics.GameGraphics;
import com.alientome.core.vecmath.Vec2;
import com.alientome.game.GameObject;
import com.alientome.game.level.Level;

public abstract class Particle extends GameObject {

    protected final Level level;
    protected final Vec2 velocity;
    protected int time;

    protected Particle(Level level, Vec2 pos, Vec2 velocity, int time) {

        super(pos);

        this.level = level;
        this.velocity = velocity;
        this.time = time;
    }

    @Override
    protected AxisAlignedBoundingBox boundingBox() {
        return null;
    }

    @Override
    public boolean canBeCollidedWith() {
        return false;
    }

    @Override
    protected void drawDebug(GameGraphics g) {
    }

    @Override
    protected void drawSpecial(GameGraphics g, int x, int y) {
    }

    public void update() {

        if (time > 0)
            time--;
        else
            level.removeParticle(this);

        pos.add(velocity);
    }
}
