package com.alientome.game.collisions;

import com.alientome.core.vecmath.Vec2;

import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * An immutable, non-moving implementation of the <code>AxisAlignedBoundingBox</code>
 */
public class StaticBoundingBox extends AbstractBoundingBox {

    /**
     * The x position of the left edge.
     */
    private final double minX;

    /**
     * The x position of the right edge.
     */
    private final double maxX;

    /**
     * The y position of the top edge.
     */
    private final double minY;

    /**
     * The y position of the bottom edge.
     */
    private final double maxY;

    public StaticBoundingBox(double x1, double y1, double x2, double y2) {

        minX = min(x1, x2);
        minY = min(y1, y2);
        maxX = max(x1, x2);
        maxY = max(y1, y2);
    }

    public StaticBoundingBox(Vec2 pos, double width, double height) {

        this(pos.getX(), pos.getY(), pos.getX() + width, pos.getY() + height);
    }

    @Override
    public double getMinX() {
        return minX;
    }

    @Override
    public double getMinY() {
        return minY;
    }

    @Override
    public double getMaxX() {
        return maxX;
    }

    @Override
    public double getMaxY() {
        return maxY;
    }

    @Override
    public double getWidth() {
        return maxX - minX;
    }

    @Override
    public double getHeight() {
        return maxY - minY;
    }

    @Override
    public double getCenterX() {
        return minX + getWidth() / 2;
    }

    @Override
    public double getCenterY() {
        return minY + getHeight() / 2;
    }
}
