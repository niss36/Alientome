package com.util.collisions;

import com.util.Line;
import com.util.Vec2;
import com.util.visual.GameGraphics;

import static com.util.profile.ExecutionTimeProfiler.theProfiler;
import static java.lang.Math.abs;

public abstract class AxisAlignedBoundingBox {

    public boolean intersects(AxisAlignedBoundingBox other) {

        return (other.getMaxX() > getMinX() && other.getMinX() < getMaxX()) && (other.getMaxY() > getMinY() && other.getMinY() < getMaxY());
    }

    public Contact processCollisionWith(AxisAlignedBoundingBox other, Vec2 velocity) {

        theProfiler.startSection("Collision Processing");

//        velocity = null;

        if (!intersects(other)) {
            theProfiler.endSection("Collision Processing");
            return null;
        }

        Vec2 thisToOther = new Vec2(
                other.getCenterX() - getCenterX(),
                other.getCenterY() - getCenterY());

        double thisHalfExtents = getWidth() / 2;
        double otherHalfExtents = other.getWidth() / 2;

        double overlapX = thisHalfExtents + otherHalfExtents - abs(thisToOther.x);

        if (overlapX > 0) {

            thisHalfExtents = getHeight() / 2;
            otherHalfExtents = other.getHeight() / 2;

            double overlapY = thisHalfExtents + otherHalfExtents - abs(thisToOther.y);

            if (overlapY > 0) {

                Vec2 normal;
                double depth;

                boolean computeX = velocity != null ? (overlapX < overlapY && abs(velocity.x) >= overlapX) || (overlapY < overlapX && abs(velocity.y) < overlapY) : overlapX < overlapY;

                if (computeX) {

                    if (thisToOther.x < 0)
                        normal = Vec2.UNIT_MINUS_X;
                    else
                        normal = Vec2.UNIT_X;

                    depth = overlapX;
                } else {

                    if (thisToOther.y < 0)
                        normal = Vec2.UNIT_MINUS_Y;
                    else
                        normal = Vec2.UNIT_Y;

                    depth = overlapY;
                }

                theProfiler.endSection("Collision Processing");
                return new Contact(normal, depth);
            }
        }

        theProfiler.endSection("Collision Processing");
        return null;
    }

    public Contact processCollisionWith(AxisAlignedBoundingBox other, Vec2 thisVelocity, Vec2 otherVelocity) {

        Vec2 relVel = thisVelocity.subtractImmutable(otherVelocity);
//        System.out.println("relVel = " + relVel);
        return processCollisionWith(other, /*relVel*/null);

        /*Vec2 thisPos = new Vec2(getMinX(), getMinY());

        AxisAlignedBoundingBox sum = new StaticBoundingBox(thisPos, getWidth() + other.getWidth(), getHeight() + other.getHeight());
        System.out.println("sum = " + sum);

        Vec2 otherPos = new Vec2(other.getMinX(), other.getMinY());
        System.out.println("otherPos = " + otherPos);

        Vec2 relativeVelocity = otherVelocity.subtractImmutable(thisVelocity);
//        relativeVelocity.normalize();
        System.out.println("relativeVelocity = " + relativeVelocity);

        double dirInvX = 1.0 / relativeVelocity.x;
        double dirInvY = 1.0 / relativeVelocity.y;
        System.out.println("dirInvX = " + dirInvX);
        System.out.println("dirInvY = " + dirInvY);

        double t1 = (sum.getMinX() - otherPos.x) * dirInvX;
        System.out.println("(sum.getMinX() - otherPos.x) = " + (sum.getMinX() - otherPos.x));
        double t2 = (sum.getMaxX() - otherPos.x) * dirInvX;
        System.out.println("(sum.getMaxX() - otherPos.x) = " + (sum.getMaxX() - otherPos.x));
        double t3 = (sum.getMinY() - otherPos.y) * dirInvY;
        System.out.println("(sum.getMinY() - otherPos.y) = " + (sum.getMinY() - otherPos.y));
        double t4 = (sum.getMaxY() - otherPos.y) * dirInvY;
        System.out.println("(sum.getMaxY() - otherPos.y) = " + (sum.getMaxY() - otherPos.y));
        System.out.println("t1 = " + t1);
        System.out.println("t2 = " + t2);
        System.out.println("t3 = " + t3);
        System.out.println("t4 = " + t4);

        double tMin = max(min(t1, t2), min(t3, t4));
        double tMax = min(max(t1, t2), max(t3, t4));
        System.out.println("tMin = " + tMin);
        System.out.println("tMax = " + tMax);

        double t;

        if (tMax < 0) return null;

        if (tMin > tMax) return null;

        t = tMin;
        System.out.println("t = " + t);

        Vec2 normal = null;
        if (t == t1) normal = Vec2.UNIT_MINUS_X;
        else if (t == t2) normal = Vec2.UNIT_X;
        else if (t == t3) normal = Vec2.UNIT_MINUS_Y;
        else if (t == t4) normal = Vec2.UNIT_Y;
        System.out.println("normal = " + normal);

        *//*if (normal == null)*//*
//            throw new Error("t " + t + " t1 " + t1 + " t2 " + t2 + " t3 " + t3 + " t4 " + t4 + " tMin " + tMin + " tMax " + tMax + " normal " + normal);
        return null;*/

    }

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

    public AxisAlignedBoundingBox offset(double x, double y) {
        return new StaticBoundingBox(getMinX() + x, getMinY() + y, getMaxX() + x, getMaxY() + y);
    }

    public AxisAlignedBoundingBox offset(Vec2 vec) {
        return offset(vec.x, vec.y);
    }

    public AxisAlignedBoundingBox expand(double x, double y) {
        return new StaticBoundingBox(getMinX() - x, getMinY() - y, getMaxX() + x, getMaxY() + y);
    }

    public void draw(GameGraphics g) {
        g.graphics.drawRect((int) getMinX() - g.origin.x, (int) getMinY() - g.origin.y, (int) getWidth(), (int) getHeight());
    }

    public void fill(GameGraphics g) {
        g.graphics.fillRect((int) getMinX() - g.origin.x, (int) getMinY() - g.origin.y, (int) getWidth(), (int) getHeight());
    }

    public abstract double getMinX();

    public abstract double getMinY();

    public abstract double getMaxX();

    public abstract double getMaxY();

    public abstract double getWidth();

    public abstract double getHeight();

    public abstract double getCenterX();

    public abstract double getCenterY();

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [" + getMinX() + "; " + getMinY() + "; " + getMaxX() + "; " + getMaxY() + "]";
    }
}
