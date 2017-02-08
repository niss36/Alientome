package com.game.blocks;

class BlockHole extends Block {

    BlockHole(int x, int y, byte metadata) {
        super(x, y, HOLE, metadata);
    }

    @Override
    public boolean isOpaque() {
        return false;
    }

    @Override
    public boolean isTransparent() {
        return false;
    }
}
