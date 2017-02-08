package com.game.blocks;

class BlockVoid extends Block {

    BlockVoid(int x, int y, byte metadata) {
        super(x, y, VOID, metadata);
    }

    @Override
    public boolean isOpaque() {
        return true;
    }

    @Override
    public boolean isTransparent() {
        return true;
    }
}
