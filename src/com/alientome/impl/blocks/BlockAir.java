package com.alientome.impl.blocks;

import com.alientome.game.blocks.BlockSheet;
import com.alientome.game.blocks.component.BlockTypeComponent;
import com.alientome.game.blocks.component.SimpleBlockType;
import com.alientome.game.blocks.parse.BlockState;

public class BlockAir extends BlockSheet {

    public BlockAir(int x, int y, BlockState state) {
        super(x, y, state);
    }

    @Override
    protected BlockTypeComponent createTypeComponent() {
        return new SimpleBlockType(false, false);
    }
}
