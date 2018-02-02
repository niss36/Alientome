package com.alientome.game.blocks.component;

import com.alientome.core.collisions.Contact;
import com.alientome.core.util.Direction;
import com.alientome.core.util.Vec2;
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

                rightFoot.subtract(slopeStart);

                if (rightFoot.x > 0)
                    return COLLISION;

                targetY = slopeStart.y - computeTargetY(rightFoot.x);

                if (targetY > rightFoot.y + slopeStart.y)
                    return NO_COLLISION;
                break;

            case RIGHT:
                Vec2 leftFoot = new Vec2(0, height).add(pos).add(velocity);

                leftFoot.subtract(slopeStart);

                if (leftFoot.x < 0)
                    return COLLISION;

                targetY = slopeStart.y + computeTargetY(leftFoot.x);

                if (targetY > leftFoot.y + slopeStart.y)
                    return NO_COLLISION;
                break;

            default:
                throw new RuntimeException("Unknown or illegal orientation : " + orientation);
        }

        velocity.y = targetY - pos.y - height;

        entity.setOnGround(true);

        return PROCESSED_COLLISION;
    }
}
