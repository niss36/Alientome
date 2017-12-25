package com.alientome.game.camera;

import com.alientome.core.collisions.AxisAlignedBoundingBox;
import com.alientome.core.util.Util;
import com.alientome.core.util.Vec2;

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
        return position.x + velocity.x * interpolation + xOffset;
    }

    private double getY(double interpolation) {
        return position.y + velocity.y * interpolation + yOffset;
    }

    @Override
    public void update() {

    }

    @Override
    public void transform(Point point, double interpolation, Rectangle viewBounds, AxisAlignedBoundingBox levelBounds) {

        int x = (int) Util.clamp(getX(interpolation) - viewBounds.width / 2, levelBounds.getMinX(), levelBounds.getMaxX() - viewBounds.width);
        int y = (int) Util.clamp(getY(interpolation) - viewBounds.height / 2, levelBounds.getMinY(), levelBounds.getMaxY() - viewBounds.height);

        point.move(x, y);
    }
}
