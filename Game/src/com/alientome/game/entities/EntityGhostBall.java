package com.alientome.game.entities;

import com.alientome.core.graphics.GameGraphics;
import com.alientome.core.util.Direction;
import com.alientome.core.util.Vec2;
import com.alientome.game.particles.ParticleGenerator;
import com.alientome.game.particles.ParticlePlayerCharge;

import java.awt.*;

/**
 * <code>EntityProjectile</code> thrown by the <code>EntityPlayer</code>.
 * Will travel in a straight line in the <code>Direction</code> the
 * <code>EntityPlayer</code> was facing.
 */
public class EntityGhostBall extends EntityProjectile {

    private final ParticleGenerator generator =
            new ParticleGenerator((pos, velocity, time) -> new ParticlePlayerCharge(pos, level, velocity, time, 0));

    private final boolean big;

    /**
     * @param thrower the <code>Entity</code> this was thrown by. Will be ignored in collision checks.
     * @param big     whether this <code>EntityGhostBall</code> should be bigger, slower and deal more damage.
     */
    public EntityGhostBall(Entity thrower, boolean big) {

        super(thrower, new Dimension(big ? 12 : 8, big ? 12 : 8), big ? 10 : 5);

        this.big = big;

        pos.y += 5;

        maxVelocity = big ? 7 : 10;

        if (big) handler.setAnimationUsed(1);
        else handler.setAnimationUsed(0);

        affectedByGravity = false;
    }

    @Override
    protected void preUpdateInternal() {

        super.preUpdateInternal();

        move(facing, 5);
    }

    @Override
    protected void postUpdateInternal() {

        super.postUpdateInternal();

        if (dead) {

            Vec2 center;
            if (big)
                center = new Vec2(12, 12);
            else
                center = new Vec2(8, 8);

            center.add(pos);

            for (int i = 0; i < (big ? 20 : 10); i++)
                level.spawnParticle(generator.generateCircleAway(center, big ? 25 : 20, ((i * 5) % 20) + 5));
        }
    }

    @Override
    protected void draw(GameGraphics g, int x, int y) {
        super.draw(g, x - (facing == Direction.LEFT ? 0 : dimension.width), y);
    }

    @Override
    protected double knockbackXFactor() {
        return big ? 4 : 3;
    }

    @Override
    protected void onSetDead() {
        super.onSetDead();

        deathTimer = 2;
    }

    @Override
    public String getNameKey() {
        return "entities.ghostBall.name";
    }
}
