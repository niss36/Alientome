package com.alientome.editors.animations;

import com.alientome.core.util.Direction;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class Animation {

    private final BufferedImage[] sprites;
    private final AnimationInfo info;

    private int imageUsed = 0;
    private int count = 0;

    public Animation(BufferedImage[] sprites, File source, int delay, int[] xOffsets, int[] yOffsets, boolean loop, Dimension dimension, int scale) {

        this.sprites = sprites;

        info = new AnimationInfo(source, delay, xOffsets, yOffsets, loop, dimension, scale);
    }

    public void draw(Graphics g, int x, int y, Direction direction, int scale, boolean drawOutline) {

        int drawX;
        int drawY = y + info.getYOffsets()[imageUsed] * scale;
        int drawWidth = sprites[imageUsed].getWidth() * scale;
        int drawHeight = sprites[imageUsed].getHeight() * scale;

        switch (direction) {

            case LEFT:
                drawX = x + info.getXOffsets()[imageUsed] * scale;
                break;

            case RIGHT:
                drawX = x + drawWidth;
                drawWidth = -drawWidth;
                break;

            default:
                throw new IllegalArgumentException("Illegal direction (" + direction + ") : must be either LEFT or RIGHT");
        }

        g.drawImage(sprites[imageUsed], drawX, drawY, drawWidth, drawHeight, null);

        if (drawOutline) {

            g.setColor(Color.red);
            g.drawRect(x, y, info.getDimension().width * scale, info.getDimension().height * scale);
            g.setColor(Color.black);
        }
    }

    public void update() {

        if (++count >= info.getDelay()) {
            count = 0;
            imageUsed++;
        }

        if (imageUsed >= sprites.length) {
            if (info.isLoop()) imageUsed = 0;
            else imageUsed = sprites.length - 1;
        }
    }

    public void reset() {

        imageUsed = 0;
        count = 0;
    }

    public void nextFrame() {

        imageUsed++;

        if (imageUsed >= sprites.length) {
            if (info.isLoop()) imageUsed = 0;
            else imageUsed = sprites.length - 1;
        }
    }

    public void previousFrame() {

        imageUsed--;

        if (imageUsed < 0) {
            if (info.isLoop()) imageUsed = sprites.length - 1;
            else imageUsed = 0;
        }
    }

    public Point getCenter(Dimension containerDimension, int scale) {

        int width = info.getDimension().width * scale;
        int height = info.getDimension().height * scale;

        return new Point(containerDimension.width / 2 - width / 2, containerDimension.height / 2 - height / 2);
    }

    public BufferedImage[] getSprites() {
        return sprites;
    }

    public int getCurrentFrame() {
        return imageUsed;
    }

    public int getLength() {
        return sprites.length;
    }

    public AnimationInfo getInfo() {
        return info;
    }
}
