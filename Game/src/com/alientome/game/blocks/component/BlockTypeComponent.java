package com.alientome.game.blocks.component;

import com.alientome.core.collisions.Contact;
import com.alientome.game.blocks.BlockConstants;
import com.alientome.game.entities.Entity;

public interface BlockTypeComponent extends BlockConstants {

    boolean isOpaque();

    boolean canBeCollidedWith();

    int beforeCollide(Entity entity, Contact contact);
}
