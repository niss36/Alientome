package com.game.blocks;

class BlockAir extends Block {

    BlockAir(int x, int y, byte metadata) {
        super(x, y, AIR, metadata);
    }

    @Override
    public boolean isOpaque() {
        return false;
    }

    @Override
    public boolean isTransparent() {
        return true;
    }
}
