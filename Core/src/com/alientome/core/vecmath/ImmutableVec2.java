package com.alientome.core.vecmath;

public class ImmutableVec2 extends Vec2 {

    public ImmutableVec2(double x, double y) {
        super(x, y);
    }

    public ImmutableVec2(Vec2 v) {
        super(v);
    }

    public ImmutableVec2(Vec2 v, double scale) {
        super(v, scale);
    }

    private UnsupportedOperationException newUOE() {
        return new UnsupportedOperationException("Immutable");
    }

    @Override
    public void set(Vec2 v) {
        throw newUOE();
    }

    @Override
    public void set(double x, double y) {
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
    public Vec2 sub(Vec2 v) {
        throw newUOE();
    }

    @Override
    public Vec2 scale(double d) {
        throw newUOE();
    }

    @Override
    public Vec2 divide(double d) {
        throw newUOE();
    }

    @Override
    public Vec2 addScaled(Vec2 v, double d) {
        throw newUOE();
    }

    @Override
    public void setX(double x) {
        throw newUOE();
    }

    @Override
    public void addX(double d) {
        throw newUOE();
    }

    @Override
    public void setY(double y) {
        throw newUOE();
    }

    @Override
    public void addY(double d) {
        throw newUOE();
    }
}
