package com.game.blocks;

class BlockSand extends Block {

    private final boolean isBackground;

    BlockSand(int x, int y, byte metadata) {
        super(x, y, SAND, metadata);

        isBackground = (metadata & 0x01) == 1;

        handler.setAnimationUsed(isBackground ? 1 : 0);
    }

    @Override
    public boolean isOpaque() {
        return !isBackground;
    }

    @Override
    public boolean isTransparent() {
        return isBackground;
    }
}
