package com.util.collisions;

import com.util.Vec2;

/**
 * A pseudo-mutable, locked to the provided pos, implementation of the <code>AxisAlignedBoundingBox</code>
 */
public class DynamicBoundingBox extends AxisAlignedBoundingBox {

    private final Vec2 pos;
    private final double width;
    private final double height;

    public DynamicBoundingBox(Vec2 pos, double width, double height) {

        this.pos = pos;
        this.width = width;
        this.height = height;
    }

    @Override
    public double getMinX() {
        return pos.x;
    }

    @Override
    public double getMinY() {
        return pos.y;
    }

    @Override
    public double getMaxX() {
        return pos.x + width;
    }

    @Override
    public double getMaxY() {
        return pos.y + height;
    }

    @Override
    public double getWidth() {
        return width;
    }

    @Override
    public double getHeight() {
        return height;
    }

    @Override
    public double getCenterX() {
        return pos.x + width / 2;
    }

    @Override
    public double getCenterY() {
        return pos.y + height / 2;
    }
}
