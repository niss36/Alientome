package com.alientome.game.blocks.component;

import com.alientome.core.collisions.AxisAlignedBoundingBox;
import com.alientome.core.collisions.Contact;
import com.alientome.core.util.Vec2;
import com.alientome.game.entities.Entity;

public class PlatformBlockType implements BlockTypeComponent {

    private final boolean isTopPlatform;
    private final double platformTopY;

    public PlatformBlockType(boolean isTopPlatform, AxisAlignedBoundingBox boundingBox) {
        this(isTopPlatform, boundingBox.getMinY());
    }

    public PlatformBlockType(boolean isTopPlatform, double platformTopY) {
        this.isTopPlatform = isTopPlatform;
        this.platformTopY = platformTopY;
    }

    @Override
    public boolean isOpaque() {
        return false;
    }

    @Override
    public boolean canBeCollidedWith() {
        return isTopPlatform;
    }

    @Override
    public int beforeCollide(Entity entity, Contact contact) {

        if (isTopPlatform && contact.normal == Vec2.UNIT_MINUS_Y && entity.getBoundingBox().getMaxY() <= platformTopY)
            return COLLISION;

        return NO_COLLISION;
    }
}
