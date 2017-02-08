package com.util.collisions;

import com.util.Vec2;

public class Contact {

    public final Vec2 normal;
    public final double depth;

    public Contact(Vec2 normal, double depth) {
        this.normal = normal;
        this.depth = depth;
    }

    public Contact negate() {

        return new Contact(normal.negateImmutable(), depth);
    }

    @Override
    public String toString() {
        return String.format("Contact[normal=%s, depth=%s]", normal, depth);
    }
}
