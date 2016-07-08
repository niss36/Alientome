package com.game.blocks;

import com.util.AxisAlignedBB;

abstract class BlockNonFull extends Block {

    final byte lengthX;
    final byte lengthY;
    private final byte[][] subTiles;

    /**
     * Initialize the <code>Block</code>
     *
     * @param x the x coordinate
     * @param y the y coordinate
     */
    BlockNonFull(int x, int y, byte lengthX, byte lengthY) {
        super(x, y);

        this.lengthX = lengthX;
        this.lengthY = lengthY;
        subTiles = createSubTileArray();
    }

    @Override
    protected AxisAlignedBB boundingBox() {

        AxisAlignedBB bb = super.boundingBox();

        for (byte i = 0; i < lengthX; i++) {

            for (byte j = 0; j < lengthY; j++) {

                if (subTiles[i][j] != 0) {

                    bb.add(subBoundingBox(i, j));
                }
            }
        }

        return bb;
    }

    private AxisAlignedBB subBoundingBox(byte i, byte j) {

        double x = pos.x + (double) i / lengthX * width;
        double y = pos.y + (double) j / lengthY * width;

        return new AxisAlignedBB(x, y, x + (double) width / lengthX, y + (double) width / lengthY);
    }

    byte[][] createSubTileArray() {

        return new byte[lengthX][lengthY];
    }
}
