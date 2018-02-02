package com.alientome.game;

import com.alientome.core.collisions.AxisAlignedBoundingBox;
import com.alientome.core.graphics.GameGraphics;
import com.alientome.core.vecmath.Vec2;

import java.awt.*;

public abstract class GameObject {

    /**
     * The position of this GameObject
     */
    protected final Vec2 pos;

    /**
     * The AxisAlignedBoundingBox describing the bounds of this object
     */
    protected AxisAlignedBoundingBox boundingBox;

    protected GameObject(double x, double y) {
        this(new Vec2(x, y));
    }

    protected GameObject(Vec2 pos) {
        this.pos = pos;
    }

    protected void actualizeBoundingBox() {
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
    public void draw(GameGraphics g, boolean debug) {

        int x = getInterpolatedX(g.interpolation) - g.origin.x;
        int y = getInterpolatedY(g.interpolation) - g.origin.y;

        if (canDraw()) draw(g, x, y);
        else drawSpecial(g, x, y);

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
     * @see #drawSpecial(GameGraphics, int, int)
     */
    protected abstract boolean canDraw();

    /**
     * Draw this <code>GameObject</code> using the supplied <code>Graphics</code>,
     * in the case it cannot be drawn normally
     *
     * @param g the <code>Graphics</code> to draw with
     * @param x the x coordinate scaled to the drawing surface
     * @param y the y coordinate scaled to the drawing surface
     */
    protected abstract void drawSpecial(GameGraphics g, int x, int y);

    protected int getScaledX(Point origin) {
        return (int) pos.getX() - origin.x;
    }

    protected int getScaledY(Point origin) {
        return (int) pos.getY() - origin.y;
    }

    public int getInterpolatedX(double partialTick) {
        return (int) pos.getX();
    }

    public int getInterpolatedY(double partialTick) {
        return (int) pos.getY();
    }

    //GETTERS

    public Vec2 getPos() {
        return pos;
    }

    public AxisAlignedBoundingBox getBoundingBox() {
        return boundingBox;
    }
}
