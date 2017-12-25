package com.alientome.game.collisions;

import com.alientome.core.collisions.AxisAlignedBoundingBox;
import com.alientome.core.collisions.Contact;
import com.alientome.core.collisions.Line;
import com.alientome.core.graphics.GameGraphics;
import com.alientome.core.util.Vec2;

import static java.lang.Math.*;

public abstract class AbstractBoundingBox implements AxisAlignedBoundingBox {

    @Override
    public boolean intersects(AxisAlignedBoundingBox other) {
        return (other.getMaxX() > getMinX() && other.getMinX() < getMaxX()) && (other.getMaxY() > getMinY() && other.getMinY() < getMaxY());
    }

    @Override
    public boolean intersects(Line line) {

        Vec2 p1 = line.getPos1();
        Vec2 p2 = line.getPos2();

        double minX = getMinX(), minY = getMinY(), maxX = getMaxX(), maxY = getMaxY();

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
        return x > minX && x < maxX;
    }

    @Override
    public boolean containsPoint(double x, double y) {
        return getMinX() <= x && x <= getMaxX() && getMinY() <= y && y <= getMaxY();
    }

    @Override
    public Contact processContact(AxisAlignedBoundingBox other) {

        Vec2 thisToOther = new Vec2(
                other.getCenterX() - getCenterX(),
                other.getCenterY() - getCenterY());

        double overlapX = getWidth() / 2 + other.getWidth() / 2 - abs(thisToOther.x);

        if (overlapX > 0) {

            double overlapY = getHeight() / 2 + other.getHeight() / 2 - abs(thisToOther.y);

            if (overlapY > 0) {

                Vec2 normal;
                double depth;

                if (overlapX < overlapY) {

                    if (thisToOther.x < 0)
                        normal = Vec2.UNIT_X;
                    else
                        normal = Vec2.UNIT_MINUS_X;

                    depth = overlapX;

                } else {

                    if (thisToOther.y < 0)
                        normal = Vec2.UNIT_Y;
                    else
                        normal = Vec2.UNIT_MINUS_Y;

                    depth = overlapY;
                }

                return new Contact(normal, depth);
            }
        }

        return null;
    }

    @Override
    public AxisAlignedBoundingBox offset(double x, double y) {
        return new StaticBoundingBox(getMinX() + x, getMinY() + y, getMaxX() + x, getMaxY() + y);
    }

    @Override
    public AxisAlignedBoundingBox offset(Vec2 vec) {
        return offset(vec.x, vec.y);
    }

    @Override
    public AxisAlignedBoundingBox expand(double x, double y) {
        return new StaticBoundingBox(getMinX() - x, getMinY() - y, getMaxX() + x, getMaxY() + y);
    }

    @Override
    public AxisAlignedBoundingBox union(AxisAlignedBoundingBox other) {
        return new StaticBoundingBox(min(getMinX(), other.getMinX()), min(getMinY(), other.getMinY()), max(getMaxX(), other.getMaxX()), max(getMaxY(), other.getMaxY()));
    }

    @Override
    public void draw(GameGraphics g) {
        g.graphics.drawRect((int) getMinX() - g.origin.x, (int) getMinY() - g.origin.y, (int) getWidth(), (int) getHeight());
    }

    @Override
    public void fill(GameGraphics g) {
        g.graphics.fillRect((int) getMinX() - g.origin.x, (int) getMinY() - g.origin.y, (int) getWidth(), (int) getHeight());
    }

    @Override
    public String toString() {
        return "BoundingBox[minX=" + getMinX() + " maxX=" + getMaxX() + " minY=" + getMinY() + " maxY=" + getMaxY() + "]";
    }
}
