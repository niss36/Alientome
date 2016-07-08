package com.game.level;

import com.game.blocks.Block;
import com.game.blocks.BlockConstants;

import java.awt.image.BufferedImage;

public final class LevelMap {
    private static final LevelMap ourInstance = new LevelMap();

    private Block[][] blocks;
    private int width;
    private int height;

    private LevelMap() {
    }

    public static LevelMap getInstance() {
        return ourInstance;
    }

    void init(BufferedImage mapImage) {

        BlockConstants.init();

        width = mapImage.getWidth();
        height = mapImage.getHeight();

        blocks = new Block[width][height];

        for (int x = 0; x < width; x++)
            for (int y = 0; y < height; y++)
                blocks[x][y] = Block.create(x, y, Block.parseRGB(mapImage.getRGB(x, y)));
    }

    /**
     * Returns the <code>Block</code> instance at the specified coordinates.
     * If bound checking is enabled, returns a new instance of a <code>BlockVoid</code>.
     *
     * @param x           the x coordinate
     * @param y           the y coordinate
     * @param checkBounds if bound checking should be done first
     * @return the found <code>Block</code> instance, or a new <code>BlockVoid</code>
     * instance if <code>checkBounds</code> is true and the given coordinates are out
     * of bounds.
     * @throws ArrayIndexOutOfBoundsException if <code>checkBounds</code>
     *                                        is false and the coordinates are out of bounds.
     */
    public Block getBlock(int x, int y, boolean checkBounds) {
        return (!checkBounds || checkBounds(x, y)) ? blocks[x][y] : Block.create(x, y, Block.VOID);
    }

    /**
     * @param x the x coordinate
     * @param y the y coordinate
     * @return whether the given coordinates are contained in the
     * <code>LevelMap</code>'s <code>Block</code> array.
     */
    public boolean checkBounds(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
