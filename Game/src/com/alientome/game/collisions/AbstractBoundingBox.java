package com.alientome.game.collisions;

import com.alientome.core.collisions.AxisAlignedBoundingBox;
import com.alientome.core.collisions.Contact;
import com.alientome.core.collisions.Line;
import com.alientome.core.graphics.GameGraphics;
import com.alientome.core.vecmath.Constants;
import com.alientome.core.vecmath.Vec2;

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

        double x1 = p1.getX(), x2 = p2.getX(), y1 = p1.getY(), y2 = p2.getY();

        if ((x1 <= minX && x2 <= minX) || (y1 <= minY && y2 <= minY) || (x1 >= maxX && x2 >= maxX) || (y1 >= maxY && y2 >= maxY))
            return false;

        double m = (y2 - y1) / (x2 - x1);

        double y = m * (minX - x1) + y1;
        if (y > minY && y < maxY) return true;

        y = m * (maxX - x1) + y1;
        if (y > minY && y < maxY) return true;

        double x = (minY - y1) / m + x1;
        if (x > minX && x < maxX) return true;

        x = (maxY - y1) / m + x1;
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

        double overlapX = getWidth() / 2 + other.getWidth() / 2 - abs(thisToOther.getX());

        if (overlapX > 0) {

            double overlapY = getHeight() / 2 + other.getHeight() / 2 - abs(thisToOther.getY());

            if (overlapY > 0) {

                Vec2 normal;
                double depth;

                if (overlapX < overlapY) {

                    if (thisToOther.getX() < 0)
                        normal = Constants.UNIT_X;
                    else
                        normal = Constants.UNIT_MINUS_X;

                    depth = overlapX;

                } else {

                    if (thisToOther.getY() < 0)
                        normal = Constants.UNIT_Y;
                    else
                        normal = Constants.UNIT_MINUS_Y;

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
        return offset(vec.getX(), vec.getY());
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
