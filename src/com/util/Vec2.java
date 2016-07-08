package com.util;

@SuppressWarnings("ALL")
public class Vec2 {

    public double x;
    public double y;

    public Vec2(double x, double y) {

        this.x = x;
        this.y = y;
    }

    public Vec2(Vec2 v) {
        this(v.x, v.y);
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

    public double magnitude() {

        return Math.sqrt(x * x + y * y);
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj) || (obj instanceof Vec2 && ((Vec2) obj).x == x && ((Vec2) obj).y == y);
    }
}
