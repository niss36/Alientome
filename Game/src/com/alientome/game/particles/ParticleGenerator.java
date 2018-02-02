package com.alientome.game.particles;

import com.alientome.core.util.Vec2;

import java.awt.*;
import java.util.Random;

public class ParticleGenerator {

    private final Random random = new Random();
    private final ParticleConstructor particleConstructor;

    public ParticleGenerator(ParticleConstructor particleConstructor) {

        this.particleConstructor = particleConstructor;
    }

    public Particle generateCircleTowards(Vec2 center, int minDistance, int radius, int time) {

        int angle = random.nextInt(360);
        int distance = random.nextInt(radius - minDistance) + minDistance;

        double angleRads = Math.toRadians(angle);

        double distX = distance * Math.cos(angleRads);
        double distY = distance * Math.sin(angleRads);

        Vec2 pos = new Vec2(center).add(distX, distY);

        Vec2 velocity = center.subtractImmutable(pos).divide(time);

        return particleConstructor.create(pos, velocity, time);
    }

    public Particle generateCircleAway(Vec2 center, int radius, int time) {

        int angle = random.nextInt(360);

        double angleRads = Math.toRadians(angle);

        double distX = radius * Math.cos(angleRads);
        double distY = radius * Math.sin(angleRads);

        Vec2 pos = new Vec2(center);
        Vec2 velocity = new Vec2(distX, distY).divide(time);

        return particleConstructor.create(pos, velocity, time);
    }

    public Particle generateRectAway(Vec2 topLeft, Dimension dimension, int range, int time) {

        int angle = random.nextInt(360);

        double angleRads = Math.toRadians(angle);

        double distX = range * Math.cos(angleRads);
        double distY = range * Math.sin(angleRads);

        double x = topLeft.x + random.nextInt(dimension.width);
        double y = topLeft.y + random.nextInt(dimension.height);

        Vec2 velocity = new Vec2(distX, distY).divide(time);

        return particleConstructor.create(new Vec2(x, y), velocity, time);
    }
}
