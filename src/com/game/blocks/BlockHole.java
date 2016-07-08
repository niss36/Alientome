package com.game.blocks;

public class BlockHole extends Block {

    public BlockHole(int x, int y) {
        super(x, y);
    }

    @Override
    public boolean isOpaque() {
        return false;
    }

    @Override
    public boolean isTransparent() {
        return false;
    }

    @Override
    public byte getIndex() {
        return HOLE;
    }
}
