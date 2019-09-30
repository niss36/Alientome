package com.alientome.editors.animations;

import com.alientome.core.util.Direction;
import com.alientome.visual.animations.Animation;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class ExtAnimation implements Animation {

    private final File source;

    private Animation animation;
    private Dimension dimension;
    private int scale;

    private int imageUsed = 0;
    private int count = 0;

    public ExtAnimation(File source, Animation animation, Dimension dimension, int scale) {

        this.source = source;
        this.animation = animation;
        this.dimension = dimension;
        this.scale = scale;
    }

    public void draw(Graphics g, int x, int y, Direction direction, int scale, boolean drawOutline) {

        BufferedImage[] sprites = getSprites();

        int drawX;
        int drawY = y + getYOffsets()[imageUsed] * scale;
        int drawWidth = sprites[imageUsed].getWidth() * scale;
        int drawHeight = sprites[imageUsed].getHeight() * scale;

        switch (direction) {

            case LEFT:
                drawX = x + getXOffsets()[imageUsed] * scale;
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
            g.drawRect(x, y, dimension.width * scale, dimension.height * scale);
            g.setColor(Color.black);
        }
    }

    public void update() {

        if (++count >= getDelay()) {
            count = 0;
            imageUsed++;
        }

        if (imageUsed >= getSprites().length) {
            if (isLoop()) imageUsed = 0;
            else imageUsed = getSprites().length - 1;
        }
    }

    public void reset() {

        imageUsed = 0;
        count = 0;
    }

    public void nextFrame() {

        imageUsed++;

        if (imageUsed >= getSprites().length) {
            if (isLoop()) imageUsed = 0;
            else imageUsed = getSprites().length - 1;
        }
    }

    public void previousFrame() {

        imageUsed--;

        if (imageUsed < 0) {
            if (isLoop()) imageUsed = getSprites().length - 1;
            else imageUsed = 0;
        }
    }

    public Point getCenter(Dimension containerDimension, int scale) {

        int width = dimension.width * scale;
        int height = dimension.height * scale;

        return new Point(containerDimension.width / 2 - width / 2, containerDimension.height / 2 - height / 2);
    }

    public File getSource() {
        return source;
    }

    public int getCurrentFrame() {
        return imageUsed;
    }

    @Override
    public void draw(Graphics g, int x, int y, Direction direction, int imageUsed) {
        animation.draw(g, x, y, direction, imageUsed);
    }

    @Override
    public int getLength() {
        return animation.getLength();
    }

    @Override
    public BufferedImage[] getSprites() {
        return animation.getSprites();
    }

    @Override
    public int getDelay() {
        return animation.getDelay();
    }

    @Override
    public int[] getXOffsets() {
        return animation.getXOffsets();
    }

    @Override
    public int[] getYOffsets() {
        return animation.getYOffsets();
    }

    @Override
    public boolean isLoop() {
        return animation.isLoop();
    }
}
