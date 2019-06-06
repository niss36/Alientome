package com.alientome.core.collisions;

import com.alientome.core.graphics.GameGraphics;
import com.alientome.core.vecmath.Vec2;

/**
 * Represents a line segment. Notably used for line of sight tests.
 */
public interface Line {

    /**
     * @return one end of the segment.
     */
    Vec2 getPos1();

    /**
     * @return the other end of the segment.
     */
    Vec2 getPos2();

    /**
     * Draws this line segment using the given Graphics.
     * @param g the GameGraphics to draw with.
     */
    void draw(GameGraphics g);
}
