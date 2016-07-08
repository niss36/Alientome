package com.game.blocks;

public class BlockSand extends Block {

    public BlockSand(int x, int y) {
        super(x, y);
    }

    @Override
    public boolean isOpaque() {
        return true;
    }

    @Override
    public boolean isTransparent() {
        return false;
    }

    @Override
    public byte getIndex() {
        return SAND;
    }
}
