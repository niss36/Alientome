package com.game;

import com.util.AxisAlignedBB;
import com.util.Vec2;

import java.awt.*;

/**
 * This abstract class encloses all objects in the game, ie <code>Block</code> and <code>Entity</code> objects.
 * It mainly provides generic ways to render such objects.
 */
public abstract class GameObject {

    protected final Vec2 pos;
    protected AxisAlignedBB boundingBox;

    /**
     * Constructs the <code>GameObject</code> from it's x and y coordinates.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     */
    protected GameObject(double x, double y) {
        pos = new Vec2(x, y);
    }

    protected final void actualizeBoundingBox() {
        boundingBox = boundingBox();
    }

    /**
     * @return the dynamically-calculated <code>AxisAlignedBB</code> for this object
     */
    protected abstract AxisAlignedBB boundingBox();

    /**
     * Draw this <code>GameObject</code> using the supplied <code>Graphics</code>
     *
     * @param g     the <code>Graphics</code> to draw with
     * @param min   the relative origin
     * @param debug whether debug info should be drawn
     */
    public final void draw(Graphics g, Point min, boolean debug) {

        int x = (int) pos.x - min.x;
        int y = (int) pos.y - min.y;

        if (drawCondition()) draw(g, x, y);
        else drawSpecial(g, min);

        if (debug) drawDebug(g, min);
    }

    /**
     * Draws this <code>GameObject</code>'s main features
     *
     * @param g the <code>Graphics</code> to draw with
     * @param x the x coordinate scaled to the drawing surface
     * @param y the y coordinate scaled to the drawing surface
     */
    protected abstract void draw(Graphics g, int x, int y);

    /**
     * Draws this <code>GameObject</code>'s debug info. Called only in debug mode.
     *
     * @param g   the <code>Graphics</code> to draw with
     * @param min the relative origin
     */
    protected abstract void drawDebug(Graphics g, Point min);

    /**
     * @return whether the normal drawing process should happen.
     * Else, invoke <code>drawSpecial</code>.
     * @see #drawSpecial(Graphics, Point)
     */
    protected abstract boolean drawCondition();

    /**
     * Draw this <code>GameObject</code> using the supplied <code>Graphics</code>,
     * in the case it cannot be drawn normally
     *
     * @param g   the <code>Graphics</code> to draw with
     * @param min the relative origin
     */
    protected abstract void drawSpecial(Graphics g, Point min);

    //GETTERS & SETTERS

    public final Vec2 getPos() {
        return pos;
    }

    public final AxisAlignedBB getBoundingBox() {
        return boundingBox;
    }

    /**
     * @return a new <code>Vec2d</code> containing this <code>GameObject</code>'s coordinates
     */
    public final Vec2 getPosVec() {

        return new Vec2(pos);
    }
}
