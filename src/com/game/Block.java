package com.game;

import com.game.level.Level;
import com.util.AxisAlignedBB;

import java.awt.*;

/**
 * Used to represent tiles in the <code>Level</code>.
 */
public class Block {

    public static int width;
    private final int x;
    private final int y;
    private final int index;

    private final AxisAlignedBB boundingBox;

    /**
     * Initialize this <code>Block</code>
     *
     * @param x the x coordinate
     * @param y the y coordinate
     */
    public Block(int x, int y) {

        this.x = x;
        this.y = y;

        index = Level.getInstance().get(x, y);

        boundingBox = new AxisAlignedBB(x * width, y * width, x * width + width, y * width + width);
    }

    /**
     * @param p the <code>Point</code> to get coordinates from
     */
    public Block(Point p) {
        this(p.x, p.y);
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
     * Draw this <code>Block</code> using the supplied <code>Graphics</code>
     *
     * @param g     the <code>Graphics</code> to draw with
     * @param min   the relative origin
     * @param debug whether debug info should be drawn
     */
    public void draw(Graphics g, Point min, boolean debug) {

        switch (index) {

            case Integer.MIN_VALUE: {
                g.setColor(Color.white);
                break;
            }

            case -1: {

                g.setColor(Color.darkGray);
                break;
            }

            case 0: {

                g.setColor(Color.blue);
                break;
            }

            case 1: {

                g.setColor(Color.green);
                break;
            }
        }

        int x = this.x * width - min.x;
        int y = this.y * width - min.y;

        //noinspection SuspiciousNameCombination
        g.fillRect(x, y, width, width);

        if (debug) {
            g.setColor(Color.red);

            g.drawString("" + index, x + 2, y + 12);

            //noinspection SuspiciousNameCombination
            g.drawRect(x, y, width, width);
        }
    }

    /**
     * @return whether this <code>Block</code> can't be seen and passed through
     */
    public boolean isOpaque() {
        return !(index == 0 || index == -1);
    }

    //GETTERS AND SETTERS

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getIndex() {
        return index;
    }

    public AxisAlignedBB getBoundingBox() {
        return boundingBox;
    }

    @Override
    public String toString() {
        return "Block [" + x + ";" + y + "] " + index;
    }
}
