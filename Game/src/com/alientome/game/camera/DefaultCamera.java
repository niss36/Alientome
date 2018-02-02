package com.alientome.game.camera;

import com.alientome.core.collisions.AxisAlignedBoundingBox;
import com.alientome.core.util.MathUtils;
import com.alientome.core.vecmath.Vec2;

import java.awt.*;

public class DefaultCamera implements Camera {

    private Vec2 position, velocity;
    private double xOffset, yOffset;

    public DefaultCamera(Vec2 position, Vec2 velocity, double xOffset, double yOffset) {
        this.position = position;
        this.velocity = velocity;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
    }

    private double getX(double interpolation) {
        return position.getX() + velocity.getX() * interpolation + xOffset;
    }

    private double getY(double interpolation) {
        return position.getY() + velocity.getY() * interpolation + yOffset;
    }

    @Override
    public void update() {

    }

    @Override
    public void transform(Point point, double interpolation, Rectangle viewBounds, AxisAlignedBoundingBox levelBounds) {

        int x = (int) MathUtils.clamp(getX(interpolation) - viewBounds.width / 2, levelBounds.getMinX(), levelBounds.getMaxX() - viewBounds.width);
        int y = (int) MathUtils.clamp(getY(interpolation) - viewBounds.height / 2, levelBounds.getMinY(), levelBounds.getMaxY() - viewBounds.height);

        point.move(x, y);
    }
}
