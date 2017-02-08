package com.util;

//@SuppressWarnings("ALL")
public class Vec2 {

    public static final Vec2 UNIT_X = immutableVec2(1, 0);
    public static final Vec2 UNIT_MINUS_X = immutableVec2(-1, 0);
    public static final Vec2 UNIT_Y = immutableVec2(0, 1);
    public static final Vec2 UNIT_MINUS_Y = immutableVec2(0, -1);
    public double x;
    public double y;

    public Vec2() {
        this(0, 0);
    }

    public Vec2(double x, double y) {

        this.x = x;
        this.y = y;
    }

    public Vec2(Vec2 v) {
        this(v.x, v.y);
    }

    // Mainly for the dev(s) not to inadvertently add or sub unit vectors, not fully functional though.
    private static Vec2 immutableVec2(double x, double y) {

        return new Vec2(x, y) {
            @Override
            public void set(double x, double y) {
                throw new UnsupportedOperationException("Immutable");
            }

            @Override
            public Vec2 add(Vec2 v) {
                throw new UnsupportedOperationException("Immutable");
            }

            @Override
            public Vec2 subtract(Vec2 v) {
                throw new UnsupportedOperationException("Immutable");
            }

            @Override
            public Vec2 negate() {
                throw new UnsupportedOperationException("Immutable");
            }

            @Override
            public Vec2 abs() {
                throw new UnsupportedOperationException("Immutable");
            }
        };
    }

    public void set(double x, double y) {

        this.x = x;
        this.y = y;
    }

    public void set(Vec2 v) {

        set(v.x, v.y);
    }

    public double distance(Vec2 v) {
        return Math.sqrt(distanceSq(v));
    }

    public double distanceSq(Vec2 v) {

        double dx = v.x - x;
        double dy = v.y - y;

        return dx * dx + dy * dy;
    }

    public double distanceSq(double x, double y) {

        double dx = x - this.x;
        double dy = y - this.y;

        return dx * dx + dy * dy;
    }

    public Vec2 add(Vec2 v) {

        x += v.x;
        y += v.y;

        return this;
    }

    public Vec2 subtract(Vec2 v) {

        x -= v.x;
        y -= v.y;

        return this;
    }

    public Vec2 subtractImmutable(Vec2 v) {
        return new Vec2(x - v.x, y - v.y);
    }

    public Vec2 multiplyImmutable(double d) {
        return new Vec2(x * d, y * d);
    }

    public Vec2 negate() {

        x = -x;
        y = -y;

        return this;
    }

    public Vec2 negateImmutable() {
        return new Vec2(-x, -y);
    }

    public double length() {
        return Math.sqrt(x * x + y * y);
    }

    public Vec2 normalize() {

        double length = length();
        x /= length;
        y /= length;

        return this;
    }

    public double dotProduct(Vec2 v) {
        return x * v.x + y * v.y;
    }

    public Vec2 abs() {

        x = Math.abs(x);
        y = Math.abs(y);

        return this;
    }

    public Vec2 absImmutable() {
        return new Vec2(Math.abs(x), Math.abs(y));
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj) || (obj instanceof Vec2 && ((Vec2) obj).x == x && ((Vec2) obj).y == y);
    }

    @Override
    public String toString() {
        return String.format("Vec2[x=%s; y=%s]", x, y);
    }

}
