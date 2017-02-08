package com.game.blocks;

import com.game.GameObject;
import com.util.collisions.AxisAlignedBoundingBox;
import com.util.collisions.StaticBoundingBox;
import com.util.visual.AnimationsHandler;
import com.util.visual.GameGraphics;

import java.awt.*;

/**
 * Used to represent tiles in the <code>Level</code>.
 */
public abstract class Block extends GameObject implements BlockConstants {

    public static final int WIDTH = 64;
    public final int blockX;
    public final int blockY;
    final byte metadata;
    final AnimationsHandler handler;
    private final byte index;
    private final String debugInfo;

    /**
     * Initialize the <code>Block</code>
     *
     * @param x        the x coordinate
     * @param y        the y coordinate
     * @param index    the index of the <code>Block</code>
     * @param metadata the metadata of this <code>Block</code>
     */
    Block(int x, int y, byte index, byte metadata) {

        super(x * WIDTH, y * WIDTH);

        this.blockX = x;
        this.blockY = y;
        this.index = index;
        this.metadata = metadata;
        debugInfo = index + ":" + metadata;

        actualizeBoundingBox();

        handler = new AnimationsHandler(getClass());
    }

    /**
     * Creates a <code>Block</code> using given arguments.
     *
     * @param x       the x coordinate
     * @param y       the y coordinate
     * @param builder a <code>BlockBuilder</code> instance containing the <code>Block</code>'s index and metadata. Can be null
     * @return a new <code>Block</code> instance from given arguments
     */
    public static Block create(int x, int y, BlockBuilder builder) {

        Block block;

        if (builder == null) {
            block = new BlockVoid(x, y, VOID);
            return block;
        }

        switch (builder.index) {

            case AIR:
                block = new BlockAir(x, y, builder.metadata);
                break;

            case SAND:
                block = new BlockSand(x, y, builder.metadata);
                break;

            case HOLE:
                block = new BlockHole(x, y, builder.metadata);
                break;

            default:
                throw new IllegalArgumentException("Unknown index value : " + builder.index);
        }

        return block;
    }

    /**
     * @return whether this <code>Block</code> prevents moving through it
     */
    public abstract boolean isOpaque();

    /**
     * @return whether this <code>Block</code> can be seen through
     */
    public abstract boolean isTransparent();

    /**
     * @return this <code>Block</code>'s index
     */
    public byte getIndex() {
        return index;
    }

    @SuppressWarnings("SuspiciousNameCombination")
    @Override
    protected AxisAlignedBoundingBox boundingBox() {
        return new StaticBoundingBox(pos, WIDTH, WIDTH);
    }

    @Override
    public boolean canBeCollidedWith() {
        return isOpaque();
    }

    @Override
    protected void draw(GameGraphics g, int x, int y) {

        handler.draw(g, x, y);
    }

    @Override
    protected void drawDebug(GameGraphics g) {

        int x = getScaledX(g.origin);
        int y = getScaledY(g.origin);

        g.graphics.setColor(Color.red);

        g.graphics.drawString(debugInfo, x + 2, y + 12);

        boundingBox.draw(g);
    }

    @Override
    protected boolean drawCondition() {

        return handler.canDraw();
    }

    @Override
    protected void drawSpecial(GameGraphics g) {
        if (!isTransparent()) boundingBox.fill(g);
    }

    @Override
    public String toString() {
        return String.format("Block [blockX=%s, blockY=%s, index=%s, metadata=%s]", blockX, blockY, index, metadata);
    }
}
