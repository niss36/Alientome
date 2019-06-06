package com.alientome.game.collisions;

import com.alientome.core.collisions.Line;
import com.alientome.core.graphics.GameGraphics;
import com.alientome.core.vecmath.Vec2;

import static com.alientome.core.util.Colors.LINE_NO_SEE;
import static com.alientome.core.util.Colors.LINE_SEE;

public class SightLine implements Line {

    /**
     * A vector representing one endpoint of the segment.
     */
    private final Vec2 pos1;

    /**
     * A vector representing the other endpoint of the segment.
     */
    private final Vec2 pos2;

    /**
     * Indicates whether the line of sight is cleared or not.
     */
    private boolean cleared = false;

    /**
     * @param pos1 first end point.
     * @param pos2 second end point.
     */
    public SightLine(Vec2 pos1, Vec2 pos2) {

        this.pos1 = pos1;
        this.pos2 = pos2;
    }

    @Override
    public Vec2 getPos1() {
        return pos1;
    }

    @Override
    public Vec2 getPos2() {
        return pos2;
    }

    /**
     * {@inheritDoc}
     * The line drawn will be green if {@link #cleared} is true, and red otherwise.
     */
    @Override
    public void draw(GameGraphics g) {

        g.graphics.setColor(cleared ? LINE_SEE : LINE_NO_SEE);
        g.graphics.drawLine((int) pos1.getX() - g.origin.x, (int) pos1.getY() - g.origin.y, (int) pos2.getX() - g.origin.x, (int) pos2.getY() - g.origin.y);
    }

    public void setCleared(boolean cleared) {
        this.cleared = cleared;
    }
}
