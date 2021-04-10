package com.alientome.core.util;

import com.alientome.core.vecmath.Vec2;

public class MathUtils {

    private MathUtils() {

    }

    /**
     * Function used to decrease velocity, that is, make it closer to zero.
     *
     * @param toDecrease the number to be decreased.
     * @param decreaseBy      the amount to decrease.
     * @return If <code>toDecrease==0</code> 0 else <code>toDecrease</code> closer to 0 by <code>decreaseBy</code>.
     */
    public static double decrease(double toDecrease, double decreaseBy) {
        return Math.abs(toDecrease) - decreaseBy <= 0 ? 0 : toDecrease < 0 ? toDecrease + decreaseBy : toDecrease - decreaseBy;
    }

    /**
     * Function used to decrease a vector representing velocity.
     *
     * @param vec   the <code>Vec2</code> to be decreased.
     * @param decreaseBy the amount to decrease.
     */
    public static void decrease(Vec2 vec, double decreaseBy) {

        vec.setX(decrease(vec.getX(), decreaseBy));
        vec.setY(decrease(vec.getY(), decreaseBy));
    }

    public static double clamp(double value, double minVal, double maxVal) {

        if (maxVal < minVal)
            throw new IllegalArgumentException(
                    "Minimum value (" + minVal + ") is greater than maximum value(" + maxVal + ")");

        return value < minVal ? minVal : value > maxVal ? maxVal : value;
    }

    public static int clamp(int value, int minVal, int maxVal) {

        if (maxVal < minVal)
            throw new IllegalArgumentException(
                    "Minimum value (" + minVal + ") is greater than maximum value(" + maxVal + ")");

        return value < minVal ? minVal : value > maxVal ? maxVal : value;
    }

    public static double lerp(double start, double end, double t) {
        return start + t * (end - start);
    }

    public static Vec2 lerpVec2(Vec2 start, Vec2 end, double t) {
        return new Vec2(lerp(start.getX(), end.getX(), t), lerp(start.getY(), end.getY(), t));
    }

    public static double diagonalDistance(Vec2 pos0, Vec2 pos1) {

        double dx = pos1.getX() - pos0.getX(), dy = pos1.getY() - pos0.getY();
        return Math.max(Math.abs(dx), Math.abs(dy));
    }

    public static double roundClosest(double toRound, double step) {

        if (step <= 0) throw new IllegalArgumentException("Step cannot be <= 0");

        return Math.round(toRound / step) * step;
    }
}
