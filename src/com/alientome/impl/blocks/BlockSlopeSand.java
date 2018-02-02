package com.alientome.impl.blocks;

import com.alientome.core.util.Direction;
import com.alientome.core.util.Vec2;
import com.alientome.game.blocks.BlockSheet;
import com.alientome.game.blocks.component.BlockTypeComponent;
import com.alientome.game.blocks.component.SimpleSlopeBlockType;
import com.alientome.game.blocks.parse.BlockState;

public class BlockSlopeSand extends BlockSheet {

    public BlockSlopeSand(int x, int y, BlockState state) {
        super(x, y, state);

        handler.setVariantUsed(0, state.metadata);
    }

    private Vec2 getSlopeStart() {

        Vec2[] offsets = {
                new Vec2(WIDTH - 1, 0), new Vec2(WIDTH - 1, 0), new Vec2(2 * WIDTH - 1, 0),
                new Vec2(), new Vec2(), new Vec2(-WIDTH, 0)
        };

        return pos.addImmutable(offsets[metadata]);
    }

    private Direction getOrientation() {
        return metadata < 3 ? Direction.LEFT : Direction.RIGHT;
    }

    private double getM() {

        double[] ms = {1.0, 0.5, 0.5};

        return ms[metadata % 3];
    }

    @Override
    protected BlockTypeComponent createTypeComponent() {
        return new SimpleSlopeBlockType(getSlopeStart(), getOrientation(), getM(), true);
    }
}
