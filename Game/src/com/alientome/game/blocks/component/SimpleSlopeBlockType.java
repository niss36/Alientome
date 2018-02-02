package com.alientome.game.blocks.component;

import com.alientome.core.util.Direction;
import com.alientome.core.vecmath.Vec2;

public class SimpleSlopeBlockType extends SlopeBlockType {

    private final double m;
    private final boolean isOpaque;

    public SimpleSlopeBlockType(Vec2 slopeStart, Direction orientation, double m, boolean isOpaque) {
        super(slopeStart, orientation);
        this.m = m;
        this.isOpaque = isOpaque;
    }

    @Override
    public boolean isOpaque() {
        return isOpaque;
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    @Override
    protected double computeTargetY(double x) {
        return m * x;
    }
}
