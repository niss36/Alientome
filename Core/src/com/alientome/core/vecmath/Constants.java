package com.alientome.core.vecmath;

public class Constants {

    public static final Vec2 UNIT_X = new ImmutableVec2(1, 0) {
        @Override
        public Vec2 negated() {
            return UNIT_MINUS_X;
        }
    };

    public static final Vec2 UNIT_Y = new ImmutableVec2(0, 1) {
        @Override
        public Vec2 negated() {
            return UNIT_MINUS_Y;
        }
    };

    public static final Vec2 UNIT_MINUS_X = new ImmutableVec2(-1, 0) {
        @Override
        public Vec2 negated() {
            return UNIT_X;
        }
    };

    public static final Vec2 UNIT_MINUS_Y = new ImmutableVec2(0, -1) {
        @Override
        public Vec2 negated() {
            return UNIT_Y;
        }
    };
}
