package com.alientome.game.collisions;

import com.alientome.core.vecmath.Vec2;

/**
 * A pseudo-mutable, locked to the provided pos, implementation of the <code>AxisAlignedBoundingBox</code>
 */
public class DynamicBoundingBox extends AbstractBoundingBox {

    /**
     * The position vector of the top-left corner.
     */
    private final Vec2 pos;

    /**
     * The width, in pixels, of the bounding-box starting from pos. Positive values are oriented towards the right.
     */
    private final double width;

    /**
     * The width, in pixels, of the bounding-box starting from pos. Positive values are oriented towards the bottom.
     */
    private final double height;

    public DynamicBoundingBox(Vec2 pos, double width, double height) {

        this.pos = pos;
        this.width = width;
        this.height = height;
    }

    @Override
    public double getMinX() {
        return pos.getX();
    }

    @Override
    public double getMinY() {
        return pos.getY();
    }

    @Override
    public double getMaxX() {
        return pos.getX() + width;
    }

    @Override
    public double getMaxY() {
        return pos.getY() + height;
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
        return pos.getX() + width / 2;
    }

    @Override
    public double getCenterY() {
        return pos.getY() + height / 2;
    }
}
