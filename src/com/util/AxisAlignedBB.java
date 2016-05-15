package com.util;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class AxisAlignedBB {

    private final double minX;
    private final double maxX;
    private final double minY;
    private final double maxY;

    public AxisAlignedBB(double x1, double y1, double x2, double y2) {

        minX = min(x1, x2);
        minY = min(y1, y2);
        maxX = max(x1, x2);
        maxY = max(y1, y2);
    }

    public boolean intersectsWith(AxisAlignedBB other) {

        return (other.maxX > this.minX && other.minX < this.maxX) && (other.maxY > this.minY && other.minY < this.maxY);
    }

    public Side intersectionSideWith(AxisAlignedBB other) {

        double wy = (width() + other.width()) * (centerY() - other.centerY());
        double hx = (height() + other.height()) * (centerX() - other.centerX());

        if (wy > hx)
            if (wy > -hx)
                return Side.TOP;
            else
                return Side.RIGHT;
        else if (wy > -hx)
            return Side.LEFT;
        else
            return Side.BOTTOM;
    }

    public AxisAlignedBB offset(double x, double y) {
        return new AxisAlignedBB(minX + x, minY + y, maxX + x, maxY + y);
    }

    public AxisAlignedBB expand(double x, double y) {
        return new AxisAlignedBB(minX - x, minY - y, maxX + x, maxY + y);
    }

    private double width() {
        return maxX - minX;
    }

    private double height() {
        return maxY - minY;
    }

    private double centerX() {
        return minX + width() / 2;
    }

    private double centerY() {
        return minY + height() / 2;
    }

    public String toString() {
        return "[" + minX + ";" + minY + "] -> [" + maxX + ";" + maxY + "]";
    }
}
