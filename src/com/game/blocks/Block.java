package com.game.blocks;

import com.game.GameObject;
import com.util.AxisAlignedBB;
import com.util.visual.AnimationsHandler;

import java.awt.*;

/**
 * Used to represent tiles in the <code>Level</code>.
 */
public abstract class Block extends GameObject implements BlockConstants {

    public static int width;
    public final int blockX;
    public final int blockY;
    private final AnimationsHandler handler;

    /**
     * Initialize the <code>Block</code>
     *
     * @param x the x coordinate
     * @param y the y coordinate
     */
    Block(int x, int y) {

        super(x * width, y * width);

        this.blockX = x;
        this.blockY = y;

        handler = new AnimationsHandler(getClass());
    }

    /**
     * Static method to initialize the static width of <code>Block</code>s.
     *
     * @param width the width value to be used
     */
    public static void init(int width) {
        Block.width = width;
    }

    /**
     * Converts the given rgb code to a <code>Block</code> index.
     *
     * @param rgb a 32-bits rgb code
     * @return the corresponding index
     */
    public static byte parseRGB(int rgb) {

        return rgbConvert.containsKey(rgb) ? rgbConvert.get(rgb) : VOID;
    }

    /**
     * Creates a <code>Block</code> using given arguments.
     *
     * @param x     the x coordinate
     * @param y     the y coordinate
     * @param index the index of the <code>Block</code>
     * @return a new <code>Block</code> instance from given arguments
     */
    public static Block create(int x, int y, int index) {

        Block block;

        switch (index) {

            case AIR:
                block = new BlockAir(x, y);
                break;

            case SAND:
                block = new BlockSand(x, y);
                break;

            case HOLE:
                block = new BlockHole(x, y);
                break;

            case PILLAR:
                block = new BlockPillar(x, y);
                break;

            case SAND_BACKGROUND:
                block = new BlockSandBackground(x, y);
                break;

            default:
                block = new BlockVoid(x, y);
                break;
        }

        block.actualizeBoundingBox();

        return block;
    }

    /**
     * @return whether this <code>Block</code> can't be passed through
     */
    public abstract boolean isOpaque();

    /**
     * @return whether this <code>Block</code> has no texture
     */
    public abstract boolean isTransparent();

    /**
     * @return this <code>Block</code>'s index
     */
    public abstract byte getIndex();

    /**
     * Abstract method to create the info necessary to animation loading.
     *
     * @return an <code>AnimationInfo</code> containing the info for this <code>Block</code>'s animation
     */
//    protected abstract AnimationInfo createAnimationInfo();
    @Override
    protected AxisAlignedBB boundingBox() {
        return new AxisAlignedBB(pos.x, pos.y, pos.x + width, pos.y + width);
    }

    @Override
    protected void draw(Graphics g, int x, int y) {

        /*animation.draw(g, x, y);*/
        handler.draw(g, x, y);
    }

    @Override
    protected void drawDebug(Graphics g, Point min) {

        int x = (int) pos.x - min.x;
        int y = (int) pos.y - min.y;

        g.setColor(Color.red);

        g.drawString("" + getIndex(), x + 2, y + 12);

        boundingBox.draw(g, min);
    }

    @Override
    protected boolean drawCondition() {

        return handler.canDraw();
    }

    @Override
    protected void drawSpecial(Graphics g, Point min) {
        if (!isTransparent()) {
            g.setColor(Color.black);
            boundingBox.fill(g, min);
        }
    }
}
