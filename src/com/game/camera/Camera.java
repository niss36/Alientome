package com.game.camera;

import com.util.collisions.AxisAlignedBoundingBox;

import java.awt.*;

public interface Camera {

    void update();

    void transform(Point point, double interpolation, Rectangle viewBounds, AxisAlignedBoundingBox levelBounds);
}
