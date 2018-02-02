package com.alientome.core.collisions;

import com.alientome.core.util.Vec2;

/**
 * A Contact is used to represent a way to resolve a collision.
 */
public class Contact {

    /**
     * The normal vector along which correction needs to be applied.
     */
    public final Vec2 normal;

    /**
     * The amount of correction to apply.
     */
    public final double depth;

    /**
     * @param normal the normal vector.
     * @param depth the collision depth.
     */
    public Contact(Vec2 normal, double depth) {
        this.normal = normal;
        this.depth = depth;
    }

    /**
     * Creates a new <code>Contact</code> that represents the same collision but from the perspective of the
     * other involved object. This thus reverses the normal but keeps the same depth.
     * @return a new <code>Contact</code>, relative to the other object.
     */
    public Contact reverse() {
        return new Contact(normal.negateImmutable(), depth);
    }

    @Override
    public String toString() {
        return String.format("Contact[normal=%s, depth=%s]", normal, depth);
    }
}
