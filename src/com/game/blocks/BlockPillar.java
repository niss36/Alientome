package com.game.blocks;

public class BlockPillar extends BlockNonFull {

    /**
     * Initialize the <code>Block</code>
     *
     * @param x the x coordinate
     * @param y the y coordinate
     */
    BlockPillar(int x, int y) {
        super(x, y, (byte) 3, (byte) 1);
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
        return PILLAR;
    }

    @Override
    protected byte[][] createSubTileArray() {
        byte[][] subTileArray = super.createSubTileArray();

        subTileArray[1][0] = 1;

        return subTileArray;
    }
}
