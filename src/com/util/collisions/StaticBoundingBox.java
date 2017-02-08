package com.util.collisions;

import com.util.Vec2;

import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * An immutable, non-moving implementation of the <code>AxisAlignedBoundingBox</code>
 */
public class StaticBoundingBox extends AxisAlignedBoundingBox {

    private final double minX;
    private final double maxX;
    private final double minY;
    private final double maxY;

    public StaticBoundingBox(double x1, double y1, double x2, double y2) {

        minX = min(x1, x2);
        minY = min(y1, y2);
        maxX = max(x1, x2);
        maxY = max(y1, y2);
    }

    public StaticBoundingBox(Vec2 pos, double width, double height) {

        this(pos.x, pos.y, pos.x + width, pos.y + height);
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
