package com.game;

import com.game.level.Level;
import com.util.AxisAlignedBB;

import java.awt.*;

public class Block {

    public static int width;
    private final int x;
    private final int y;
    private final int index;

    private final AxisAlignedBB boundingBox;

    public Block(int x, int y) {

        this.x = x;
        this.y = y;

        index = Level.getInstance().get(x, y);

        boundingBox = new AxisAlignedBB(x * width, y * width, x * width + width, y * width + width);
    }

    public Block(Point p) {
        this(p.x, p.y);
    }

    public static void init(int width) {

        Block.width = width;
    }

    public void draw(Graphics g, Point min, boolean debug) {

        switch (index) {

            case Integer.MIN_VALUE: {
                g.setColor(Color.white);
                break;
            }

            case -4: {

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

    public boolean isOpaque() {
        return !(index == 0 || index == -4);
    }

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
