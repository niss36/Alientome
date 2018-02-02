package com.alientome.core.vecmath;

import java.util.Objects;

public class Vec2 {

    protected double x;
    protected double y;

    public Vec2(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Vec2() {
        this(0, 0);
    }

    public Vec2(Vec2 v) {
        this(v.x, v.y);
    }

    public Vec2(Vec2 v, double scale) {
        this(v.x * scale, v.y * scale);
    }

    // --------------- STATE-CHANGING METHODS ---------------

    public void set(Vec2 v) {
        set(v.x, v.y);
    }

    public void set(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Vec2 add(Vec2 v) {
        return add(v.x, v.y);
    }

    public Vec2 add(double x, double y) {

        this.x += x;
        this.y += y;

        return this;
    }

    public Vec2 sub(Vec2 v) {

        x -= v.x;
        y -= v.y;

        return this;
    }

    public Vec2 scale(double d) {

        x *= d;
        y *= d;

        return this;
    }

    public Vec2 divide(double d) {

        x /= d;
        y /= d;

        return this;
    }

    public Vec2 addScaled(Vec2 v, double d) {

        x += v.x * d;
        y += v.y * d;

        return this;
    }

    // --------------- SAFE METHODS ---------------

    public Vec2 copy() {
        return new Vec2(this);
    }

    public Vec2 negated() {
        return new Vec2(-x, -y);
    }

    public double norm() {
        return Math.sqrt(x * x + y * y);
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

    // --------------- GETTERS & SETTERS ---------------

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void addX(double d) {
        this.x += d;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void addY(double d) {
        this.y += d;
    }

    // --------------- STANDARD METHODS ---------------

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
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
