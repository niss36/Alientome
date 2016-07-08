package com.game.blocks;

public class BlockVoid extends Block {

    public BlockVoid(int x, int y) {
        super(x, y);
    }

    @Override
    public boolean isOpaque() {
        return true;
    }

    @Override
    public boolean isTransparent() {
        return true;
    }

    @Override
    public byte getIndex() {
        return VOID;
    }
}
