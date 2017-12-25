package com.alientome.game.level;

import com.alientome.core.collisions.AxisAlignedBoundingBox;
import com.alientome.game.blocks.Block;
import com.alientome.game.collisions.StaticBoundingBox;

public class LevelMap {

    private final Block[][] blocks;
    private final AxisAlignedBoundingBox bounds;
    private final int width;
    private final int height;

    public LevelMap(Block[][] blocks) {

        this.blocks = blocks;

        width = blocks.length;
        height = blocks[0].length;

        bounds = new StaticBoundingBox(0, 0, width * Block.WIDTH, height * Block.WIDTH);
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
        return (!checkBounds || checkBounds(x, y)) ? blocks[x][y] : Block.create(x, y, null);
    }

    public Block getBlock(int x, int y) {
        return getBlock(x, y, true);
    }

    public Block getBlockAbsCoordinates(double x, double y) {
        return getBlock((int) Math.floor(x / Block.WIDTH), (int) Math.floor(y / Block.WIDTH));
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

    public AxisAlignedBoundingBox getBounds() {
        return bounds;
    }
}
