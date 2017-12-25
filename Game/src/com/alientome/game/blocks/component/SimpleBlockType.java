package com.alientome.game.blocks.component;

import com.alientome.core.collisions.Contact;
import com.alientome.game.entities.Entity;

public class SimpleBlockType implements BlockTypeComponent {

    private final boolean isOpaque;
    private final boolean canBeCollidedWith;

    public SimpleBlockType(boolean isOpaque, boolean canBeCollidedWith) {
        this.isOpaque = isOpaque;
        this.canBeCollidedWith = canBeCollidedWith;
    }

    @Override
    public boolean isOpaque() {
        return isOpaque;
    }

    @Override
    public boolean canBeCollidedWith() {
        return canBeCollidedWith;
    }

    @Override
    public int beforeCollide(Entity entity, Contact contact) {
        return COLLISION;
    }
}
