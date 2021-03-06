package com.alientome.impl.blocks;

import com.alientome.core.collisions.Contact;
import com.alientome.core.util.Vec2;
import com.alientome.game.blocks.BlockSheet;
import com.alientome.game.blocks.component.BlockTypeComponent;
import com.alientome.game.blocks.component.SimpleBlockType;
import com.alientome.game.blocks.parse.BlockState;
import com.alientome.game.collisions.AbstractBoundingBox;
import com.alientome.game.collisions.StaticBoundingBox;
import com.alientome.game.entities.Entity;
import com.alientome.game.entities.EntityLiving;

public class BlockSpikes extends BlockSheet {

    public BlockSpikes(int x, int y, BlockState state) {

        super(x, y, state);
    }

    @Override
    protected AbstractBoundingBox boundingBox() {
        return new StaticBoundingBox(pos.x, pos.y + WIDTH - 4, pos.x + WIDTH, pos.y + WIDTH);
    }

    @Override
    protected BlockTypeComponent createTypeComponent() {
        return new SimpleBlockType(false, true) {
            @Override
            public int beforeCollide(Entity entity, Contact contact) {

                if (entity instanceof EntityLiving && contact.normal.equals(Vec2.UNIT_MINUS_Y))
                    ((EntityLiving) entity).damageAbsolute(Float.MAX_VALUE);

                return COLLISION;
            }
        };
    }
}
