package com.game;

import com.game.level.Level;
import com.util.AxisAlignedBB;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Used to represent tiles in the <code>Level</code>.
 */
public class Block {

    public static int width;
    private final int x;
    private final int y;
    private final int index;

    private final AxisAlignedBB boundingBox;

    private static BufferedImage[] sprites = new BufferedImage[3];
    private static BufferedImage n = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);

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

        if(index != Integer.MIN_VALUE && sprites[index] == null) sprites[index] = getSprite();

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

        int x = this.x * width - min.x;
        int y = this.y * width - min.y;

        if(index != Integer.MIN_VALUE && sprites[index] != n) g.drawImage(sprites[index], x, y, null);

        else {
            switch (index) {

                case Integer.MIN_VALUE: {
                    g.setColor(Color.white);
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

                case 2: {

                    g.setColor(Color.darkGray);
                    break;
                }
            }

            //noinspection SuspiciousNameCombination
            g.fillRect(x, y, width, width);
        }

        if (debug) {
            g.setColor(Color.red);

            g.drawString("" + index, x + 2, y + 12);

            //noinspection SuspiciousNameCombination
            g.drawRect(x, y, width, width);
        }
    }

    private BufferedImage getSprite() {
        BufferedImage sprite = n;

        try {
            sprite = ImageIO.read(ClassLoader.getSystemResourceAsStream("Block/" + index + ".png"));
        } catch (IOException | IllegalArgumentException e) {
            e.printStackTrace();
        }

        return sprite;
    }

    /**
     * @return whether this <code>Block</code> can't be seen and passed through
     */
    public boolean isOpaque() {
        return !(index == 0 || index == 2);
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
