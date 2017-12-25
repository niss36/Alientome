package com.alientome.game.camera;

import com.alientome.core.collisions.AxisAlignedBoundingBox;
import com.alientome.core.util.Util;
import com.alientome.core.util.Vec2;

import java.awt.*;

public class TravelCamera implements Camera {

    private final Vec2 current = new Vec2();
    private final Vec2 increment = new Vec2();
    private final Vec2 end;
    private final int time;
    private boolean init = false;
    private int counter = 0;

    public TravelCamera(Vec2 end, int time) {
        this.end = end;
        this.time = time;
    }

    private double getX(double interpolation) {
        if (counter < time)
            return current.x + increment.x * interpolation;
        return current.x;
    }

    private double getY(double interpolation) {
        if (counter < time)
            return current.y + increment.y * interpolation;
        return current.y;
    }

    @Override
    public void update() {

        if (init && counter++ < time)
            current.add(increment);
    }

    private double computeX(double x, Rectangle viewBounds, AxisAlignedBoundingBox levelBounds) {
        return Util.clamp(x - viewBounds.width / 2, levelBounds.getMinX(), levelBounds.getMaxX() - viewBounds.width);
    }

    private double computeY(double y, Rectangle viewBounds, AxisAlignedBoundingBox levelBounds) {
        return Util.clamp(y - viewBounds.height / 2, levelBounds.getMinY(), levelBounds.getMaxY() - viewBounds.height);
    }

    @Override
    public void transform(Point point, double interpolation, Rectangle viewBounds, AxisAlignedBoundingBox levelBounds) {

        if (!init) {

            Vec2 start = new Vec2(point.x + viewBounds.width / 2, point.y + viewBounds.height / 2);
            Vec2 end = new Vec2(computeX(this.end.x, viewBounds, levelBounds) + viewBounds.width / 2, computeY(this.end.y, viewBounds, levelBounds) + viewBounds.height / 2);

            increment.add(end).subtract(start).divide(time);
            current.set(start);
            init = true;
        }

        int x = (int) computeX(getX(interpolation), viewBounds, levelBounds);
        int y = (int) computeY(getY(interpolation), viewBounds, levelBounds);

        point.move(x, y);
    }
}
