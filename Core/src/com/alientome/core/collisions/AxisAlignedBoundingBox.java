package com.alientome.core.collisions;

import com.alientome.core.graphics.GameGraphics;
import com.alientome.core.util.Vec2;

/**
 * <p>An AxisAlignedBoundingBox (AABB for short) is a non-rotated rectangle enclosing game objects.
 * It is used to detect and resolve collisions.
 *
 * <p>Implementations are intended to be immutable. Methods that return an AABB should always create a new instance,
 * and never modify the current instance.
 */
public interface AxisAlignedBoundingBox {

    /**
     * Checks if this box and the other one intersect.
     *
     * @param other the AABB to check intersection with
     * @return whether the two boxes intersect
     */
    boolean intersects(AxisAlignedBoundingBox other);

    /**
     * Checks if the provided line intersects with this box.
     *
     * @param line the {@link Line} to check intersection with
     * @return whether the line intersects this AABB
     */
    boolean intersects(Line line);

    /**
     * Checks if the provided point is contained in this box.
     *
     * @param x the point's x coordinate
     * @param y the point's y coordinate
     * @return whether the point is contained within box
     */
    boolean containsPoint(double x, double y);

    /**
     * <p>Processes a possible collision between the two boxes (<code>this</code> and <code>other</code>).
     * This method tries to find the collision normal and depth, by looking for the minimum translation vector.
     * The normal is always a unit vector, and is never diagonal.
     *
     * <p>This method treats <code>this</code> instance as the offending box, and the returned contact is therefore relative
     * to the other box. That is to say, the contact normal is pointing away from that box. As a result, the intended
     * collision resolution would be to push the offending object as specified by the contact normal and depth.
     *
     * <p>If there is no overlap between the two boxes, then <code>null</code> is returned.
     *
     * @param other the other AABB to check for collision
     * @return a {@link Contact} object, or null if there is no collision
     */
    Contact processContact(AxisAlignedBoundingBox other);

    /**
     * Creates a new AABB that corresponds to <code>this</code> box offset by the given amounts.
     *
     * @param x the amount to offset on the x axis
     * @param y the amount to offset on the y axis
     * @return a new AABB offset by the specified amounts
     *
     * @see #offset(Vec2)
     */
    AxisAlignedBoundingBox offset(double x, double y);

    /**
     * Offsets this box by the given vector.
     *
     * @param vec a {@link Vec2} containing the amounts to offset on each axis
     * @return a new AABB offset by the specified amounts
     *
     * @see #offset(double, double)
     */
    AxisAlignedBoundingBox offset(Vec2 vec);

    /**
     * Expands this box by the specified amounts in each direction.
     *
     * @param x the amount to expand horizontally on each side
     * @param y the amount to expand vertically on each side
     * @return a new AABB expanded by the specified amounts
     */
    AxisAlignedBoundingBox expand(double x, double y);

    /**
     * Computes and creates the union of this box and the other one; that is to say,
     * the smallest AABB enclosing both boxes.
     *
     * @param other the other box to be enclosed
     * @return a new AABB that is the union of the two boxes
     */
    AxisAlignedBoundingBox union(AxisAlignedBoundingBox other);

    /**
     * Uses the given graphics to draw this box on the screen.
     * This method takes into account the screen's origin pos.
     *
     * @param g the draw graphics
     */
    void draw(GameGraphics g);

    /**
     * Uses the given graphics to fill this box on the screen.
     * This method takes into account the screen's origin pos.
     *
     * @param g the draw graphics
     */
    void fill(GameGraphics g);

    /**
     * @return the minimum coordinate on the X axis
     */
    double getMinX();

    /**
     * @return the minimum coordinate on the Y axis
     */
    double getMinY();

    /**
     * @return the maximum coordinate on the X axis
     */
    double getMaxX();

    /**
     * @return the maximum coordinate on the Y axis
     */
    double getMaxY();

    /**
     * @return this box's width
     */
    double getWidth();

    /**
     * @return this box's height
     */
    double getHeight();

    /**
     * @return this box's center on the X axis
     */
    double getCenterX();

    /**
     * @return this box's center on the Y axis
     */
    double getCenterY();
}
