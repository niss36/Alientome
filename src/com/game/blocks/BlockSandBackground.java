package com.game.blocks;

public class BlockSandBackground extends Block {

    /**
     * Initialize the <code>Block</code>
     *
     * @param x the x coordinate
     * @param y the y coordinate
     */
    BlockSandBackground(int x, int y) {
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
        return SAND_BACKGROUND;
    }
}
