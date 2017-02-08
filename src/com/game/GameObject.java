package com.game;

import com.util.Vec2;
import com.util.collisions.AxisAlignedBoundingBox;
import com.util.visual.GameGraphics;

import java.awt.*;

/**
 * This abstract class encloses all objects in the game, ie <code>Block</code> and <code>Entity</code> objects.
 * It mainly provides generic ways to render such objects.
 */
public abstract class GameObject {

    protected final Vec2 pos;
    protected AxisAlignedBoundingBox boundingBox;

    /**
     * Constructs the <code>GameObject</code> from it's x and y coordinates.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     */
    protected GameObject(double x, double y) {
        this(new Vec2(x, y));
    }

    protected GameObject(Vec2 pos) {
        this.pos = pos;
    }

    protected final void actualizeBoundingBox() {
        boundingBox = boundingBox();
    }

    /**
     * @return the dynamically-calculated <code>StaticBoundingBox</code> for this object
     */
    protected abstract AxisAlignedBoundingBox boundingBox();

    /**
     * @return whether this <code>GameObject</code> should collide with other objects when they intersect
     */
    public abstract boolean canBeCollidedWith();

    /**
     * Draw this <code>GameObject</code> using the supplied <code>Graphics</code>
     *
     * @param g     the <code>Graphics</code> to draw with
     * @param debug whether debug info should be drawn
     */
    public final void draw(GameGraphics g, boolean debug) {

        int x = getInterpolatedX(g.interpolation) - g.origin.x;
        int y = getInterpolatedY(g.interpolation) - g.origin.y;

        if (drawCondition()) draw(g, x, y);
        else drawSpecial(g);

        if (debug) drawDebug(g);

        g.graphics.setColor(Color.black);
    }

    /**
     * Draws this <code>GameObject</code>'s main features
     *
     * @param g the <code>Graphics</code> to draw with
     * @param x the x coordinate scaled to the drawing surface
     * @param y the y coordinate scaled to the drawing surface
     */
    protected abstract void draw(GameGraphics g, int x, int y);

    /**
     * Draws this <code>GameObject</code>'s debug info. Called only in debug mode.
     *
     * @param g the <code>Graphics</code> to draw with
     */
    protected abstract void drawDebug(GameGraphics g);

    /**
     * @return whether the normal drawing process should happen.
     * Else, invoke <code>drawSpecial</code>.
     * @see #drawSpecial(GameGraphics)
     */
    protected abstract boolean drawCondition();

    /**
     * Draw this <code>GameObject</code> using the supplied <code>Graphics</code>,
     * in the case it cannot be drawn normally
     *
     * @param g the <code>Graphics</code> to draw with
     */
    protected abstract void drawSpecial(GameGraphics g);

    protected final int getScaledX(Point origin) {
        return (int) pos.x - origin.x;
    }

    protected final int getScaledY(Point origin) {
        return (int) pos.y - origin.y;
    }

    public int getInterpolatedX(double partialTick) {
        return (int) pos.x;
    }

    public int getInterpolatedY(double partialTick) {
        return (int) pos.y;
    }

    //GETTERS

    public final Vec2 getPos() {
        return pos;
    }

    public final AxisAlignedBoundingBox getBoundingBox() {
        return boundingBox;
    }
}
