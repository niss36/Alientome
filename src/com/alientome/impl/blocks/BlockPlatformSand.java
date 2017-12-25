package com.alientome.impl.blocks;

import com.alientome.game.blocks.BlockSheet;
import com.alientome.game.blocks.component.BlockTypeComponent;
import com.alientome.game.blocks.component.PlatformBlockType;
import com.alientome.game.blocks.parse.BlockState;

public class BlockPlatformSand extends BlockSheet {

    public BlockPlatformSand(int x, int y, BlockState state) {
        super(x, y, state);

        handler.setVariantUsed(0, state.metadata);
    }

    @Override
    protected BlockTypeComponent createTypeComponent() {
        return new PlatformBlockType(metadata == 1 || (metadata != 0 && metadata % 2 == 0), boundingBox);
    }
}
