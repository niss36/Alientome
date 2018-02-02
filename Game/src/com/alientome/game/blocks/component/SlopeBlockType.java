package com.alientome.game.blocks.component;

import com.alientome.core.collisions.Contact;
import com.alientome.core.util.Direction;
import com.alientome.core.vecmath.Vec2;
import com.alientome.game.entities.Entity;

public abstract class SlopeBlockType implements BlockTypeComponent {

    private final Vec2 slopeStart;
    private final Direction orientation;

    protected SlopeBlockType(Vec2 slopeStart, Direction orientation) {

        if (!orientation.horizontal)
            throw new IllegalArgumentException("Orientation must be horizontal.");

        this.slopeStart = slopeStart;
        this.orientation = orientation;
    }

    protected abstract double computeTargetY(double x);

    @Override
    public int beforeCollide(Entity entity, Contact contact) {

        int width = entity.getWidth(), height = entity.getHeight();

        Vec2 pos = entity.getPos(), velocity = entity.getVelocity();

        double targetY;
        switch (orientation) {

            case LEFT:
                Vec2 rightFoot = new Vec2(width, height).add(pos).add(velocity);

                rightFoot.sub(slopeStart);

                if (rightFoot.getX() > 0)
                    return COLLISION;

                targetY = slopeStart.getY() - computeTargetY(rightFoot.getX());

                if (targetY > rightFoot.getY() + slopeStart.getY())
                    return NO_COLLISION;
                break;

            case RIGHT:
                Vec2 leftFoot = new Vec2(0, height).add(pos).add(velocity);

                leftFoot.sub(slopeStart);

                if (leftFoot.getX() < 0)
                    return COLLISION;

                targetY = slopeStart.getY() + computeTargetY(leftFoot.getX());

                if (targetY > leftFoot.getY() + slopeStart.getY())
                    return NO_COLLISION;
                break;

            default:
                throw new RuntimeException("Unknown or illegal orientation : " + orientation);
        }

        velocity.setY(targetY - pos.getY() - height);

        entity.setOnGround(true);

        return PROCESSED_COLLISION;
    }
}
