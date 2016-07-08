package com.util;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class AxisAlignedBB {

    private final double minX;
    private final double maxX;
    private final double minY;
    private final double maxY;
    private ArrayList<AxisAlignedBB> subBoundingBoxes;
    private boolean hasChildren = false;

    public AxisAlignedBB(double x1, double y1, double x2, double y2) {

        minX = min(x1, x2);
        minY = min(y1, y2);
        maxX = max(x1, x2);
        maxY = max(y1, y2);
    }

    public void add(AxisAlignedBB sub) {

        if (!hasChildren) {
            subBoundingBoxes = new ArrayList<>();
            hasChildren = true;
        }

        subBoundingBoxes.add(sub);
    }

    public boolean intersects(AxisAlignedBB other) {

        if ((other.maxX > this.minX && other.minX < this.maxX) && (other.maxY > this.minY && other.minY < this.maxY))
            if (hasChildren)
                for (AxisAlignedBB boundingBox : subBoundingBoxes) {

                    if (boundingBox.intersects(other)) return true;
                }
            else return true;

        return false;
    }

    public CollisionPoint processCollision(AxisAlignedBB other) {

        if ((other.maxX > this.minX && other.minX < this.maxX) && (other.maxY > this.minY && other.minY < this.maxY))
            if (hasChildren)
                for (AxisAlignedBB boundingBox : subBoundingBoxes) {

                    CollisionPoint cp = boundingBox.processCollision(other);

                    if (cp != null) return cp;
                }
            else {

                Side side = intersectionSideWith(other);

                return side.isVertical() ?
                        new CollisionPoint(side, 0, getBound(side)) :
                        new CollisionPoint(side, getBound(side), 0);
            }

        return null;
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

    public boolean intersects(Line line) {

        if (hasChildren) {

            for (AxisAlignedBB boundingBox : subBoundingBoxes) {

                if (boundingBox.intersects(line)) return true;
            }
        } else {

            Point2D.Double p1 = line.getPoint1(), p2 = line.getPoint2();

            if ((p1.x <= minX && p2.x <= minX) || (p1.y <= minY && p2.y <= minY) || (p1.x >= maxX && p2.x >= maxX) || (p1.y >= maxY && p2.y >= maxY))
                return false;

            double m = (p2.y - p1.y) / (p2.x - p1.x);

            double y = m * (minX - p1.x) + p1.y;
            if (y > minY && y < maxY) return true;

            y = m * (maxX - p1.x) + p1.y;
            if (y > minY && y < maxY) return true;

            double x = (minY - p1.y) / m + p1.x;
            if (x > minX && x < maxX) return true;

            x = (maxY - p1.y) / m + p1.x;
            if (x > minX && x < maxX) return true;

        }

        return false;
    }

    public AxisAlignedBB offset(double x, double y) {
        return new AxisAlignedBB(minX + x, minY + y, maxX + x, maxY + y);
    }

    public AxisAlignedBB offset(Vec2 vec) {
        return offset(vec.x, vec.y);
    }

    public AxisAlignedBB expand(double x, double y) {
        return new AxisAlignedBB(minX - x, minY - y, maxX + x, maxY + y);
    }

    public void draw(Graphics g, Point min) {

        if (hasChildren) {

            for (AxisAlignedBB boundingBox : subBoundingBoxes) {

                boundingBox.draw(g, min);
            }

            g.setColor(Color.orange);
        }

        g.drawRect((int) minX - min.x, (int) minY - min.y, (int) width(), (int) height());
    }

    public void fill(Graphics g, Point min) {

        if (hasChildren)
            for (AxisAlignedBB boundingBox : subBoundingBoxes)
                boundingBox.fill(g, min);
        else
            g.fillRect((int) minX - min.x, (int) minY - min.y, (int) width(), (int) height());
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

    private double getBound(Side side) {

        switch (side) {

            case LEFT:
                return minX;

            case RIGHT:
                return maxX;

            case TOP:
                return minY;

            case BOTTOM:
                return maxY;
        }

        return 0;
    }
}
