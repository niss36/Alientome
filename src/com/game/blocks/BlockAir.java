package com.game.blocks;

public class BlockAir extends Block {

    public BlockAir(int x, int y) {
        super(x, y);
    }

    @Override
    public boolean isOpaque() {
        return false;
    }

    @Override
    public boolean isTransparent() {
        return true;
    }

    @Override
    public byte getIndex() {
        return AIR;
    }
}
