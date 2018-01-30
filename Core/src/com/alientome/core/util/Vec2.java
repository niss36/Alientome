package com.alientome.core.util;

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

            private UnsupportedOperationException newUOE() {
                return new UnsupportedOperationException("Immutable");
            }

            @Override
            public void set(double x, double y) {
                throw newUOE();
            }

            @Override
            public void set(Vec2 v) {
                throw newUOE();
            }

            @Override
            public Vec2 add(Vec2 v) {
                throw newUOE();
            }

            @Override
            public Vec2 add(double x, double y) {
                throw newUOE();
            }

            @Override
            public Vec2 subtract(Vec2 v) {
                throw newUOE();
            }

            @Override
            public Vec2 multiply(double d) {
                throw newUOE();
            }

            @Override
            public Vec2 divide(double d) {
                throw newUOE();
            }

            @Override
            public Vec2 normalize() {
                throw newUOE();
            }

            @Override
            public Vec2 negate() {
                throw newUOE();
            }

            @Override
            public Vec2 abs() {
                throw newUOE();
            }

            @Override
            public Vec2 addMultiplied(Vec2 v, double d) {
                throw newUOE();
            }
        };
    }

    public void set(Vec2 v) {

        set(v.x, v.y);
    }

    public void set(double x, double y) {

        this.x = x;
        this.y = y;
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

    public double dotProduct(Vec2 v) {
        return x * v.x + y * v.y;
    }

    public double length() {
        return Math.sqrt(x * x + y * y);
    }

    public Vec2 add(Vec2 v) {

        return add(v.x, v.y);
    }

    public Vec2 add(double x, double y) {

        this.x += x;
        this.y += y;

        return this;
    }

    public Vec2 addImmutable(Vec2 v) {
        return new Vec2(x + v.x, y + v.y);
    }

    public Vec2 subtract(Vec2 v) {

        x -= v.x;
        y -= v.y;

        return this;
    }

    public Vec2 subtractImmutable(Vec2 v) {
        return new Vec2(x - v.x, y - v.y);
    }

    public Vec2 multiply(double d) {

        x *= d;
        y *= d;

        return this;
    }

    public Vec2 multiplyImmutable(double d) {
        return new Vec2(x * d, y * d);
    }

    public Vec2 divide(double d) {

        x /= d;
        y /= d;

        return this;
    }

    public Vec2 divideImmutable(double d) {
        return new Vec2(x / d, y / d);
    }

    public Vec2 normalize() {
        return divide(length());
    }

    public Vec2 normalizeImmutable() {
        return divideImmutable(length());
    }

    public Vec2 negate() {

        x = -x;
        y = -y;

        return this;
    }

    public Vec2 negateImmutable() {
        return new Vec2(-x, -y);
    }

    public Vec2 abs() {

        x = Math.abs(x);
        y = Math.abs(y);

        return this;
    }

    public Vec2 absImmutable() {
        return new Vec2(Math.abs(x), Math.abs(y));
    }

    public Vec2 addMultiplied(Vec2 v, double d) {

        x += v.x * d;
        y += v.y * d;

        return this;
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
