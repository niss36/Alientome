package com.alientome.core.collisions;

import com.alientome.core.graphics.GameGraphics;
import com.alientome.core.util.Vec2;

import static com.alientome.core.util.Colors.LINE_NO_SEE;
import static com.alientome.core.util.Colors.LINE_SEE;

/**
 * Represents a line segment. Notably used for line of sight tests.
 */
public class Line {

    /**
     * A vector used as one of the segment's end points.
     */
    private final Vec2 pos1;

    /**
     * A vector used as one of the segment's end points.
     */
    private final Vec2 pos2;

    /**
     * Indicates whether the line of sight is cleared or not.
     */
    public boolean see;

    /**
     * @param pos1 first end point.
     * @param pos2 second end point.
     */
    public Line(Vec2 pos1, Vec2 pos2) {

        this.pos1 = pos1;
        this.pos2 = pos2;
    }

    public Vec2 getPos1() {
        return pos1;
    }

    public Vec2 getPos2() {
        return pos2;
    }

    /**
     * Draws this line segment using the given Graphics. It will be green if {@link #see} is true, and red otherwise.
     * @param g the GameGraphics to draw with.
     */
    public void draw(GameGraphics g) {

        g.graphics.setColor(see ? LINE_SEE : LINE_NO_SEE);
        g.graphics.drawLine((int) pos1.x - g.origin.x, (int) pos1.y - g.origin.y, (int) pos2.x - g.origin.x, (int) pos2.y - g.origin.y);
    }
}
