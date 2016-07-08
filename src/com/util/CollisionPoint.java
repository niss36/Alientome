package com.util;

public class CollisionPoint {

    private final Side collisionSide;
    private final double x;
    private final double y;

    public CollisionPoint(Side side, double x, double y) {

        collisionSide = side;
        this.x = x;
        this.y = y;
    }

    public Side getCollisionSide() {
        return collisionSide;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}
