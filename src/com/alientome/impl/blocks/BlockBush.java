package com.alientome.impl.blocks;

import com.alientome.game.blocks.BlockSheet;
import com.alientome.game.blocks.component.BlockTypeComponent;
import com.alientome.game.blocks.component.SimpleBlockType;
import com.alientome.game.blocks.parse.BlockState;

public class BlockBush extends BlockSheet {

    public BlockBush(int x, int y, BlockState state) {
        super(x, y, state);

        handler.setVariantUsed(0, state.metadata);
    }

    @Override
    protected BlockTypeComponent createTypeComponent() {
        return new SimpleBlockType(false, false);
    }
}
