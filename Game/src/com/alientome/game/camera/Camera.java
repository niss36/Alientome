package com.alientome.game.camera;

import com.alientome.core.collisions.AxisAlignedBoundingBox;

import java.awt.*;

public interface Camera {

    void update();

    void transform(Point point, double interpolation, Rectangle viewBounds, AxisAlignedBoundingBox levelBounds);
}
