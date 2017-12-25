package com.alientome.visual.animations;

import com.alientome.core.util.Direction;
import com.alientome.visual.Visual;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * An Animation is represented by an array of Images, with a set delay and loop parameters. Moreover, individual
 * positional offsets can be specified to align differently sized images with each other.
 */
public class Animation implements Visual {

    /**
     * The array of Images of this Animation.
     */
    protected final BufferedImage[] sprites;

    /**
     * The delay that should happen between each frame, in <i>game ticks</i>. Note that an Animation object has no control
     * over whether this delay is effectively applied, and shouldn't for object sharing purposes. The task of applying this
     * restriction is indeed delegated to AnimationsHandler objects.
     */
    protected final int delay;

    /**
     * The individual x offsets for each frame, in screen pixels. Those offsets are to be added as is to the position when
     * drawing images.
     */
    protected final int[] xOffsets;

    /**
     * The individual y offsets for each frame, in screen pixels. Those offsets are to be added as is to the position when
     * drawing images.
     */
    protected final int[] yOffsets;

    /**
     * Whether this Animation should restart from its first (that is, 0-indexed) frame when it reaches the end of the sequence.
     * Similarly to delay, the Animation object cannot enforce this restriction. It is delegated to AnimationsHandler objects.
     */
    protected final boolean loop;

    public Animation(BufferedImage[] sprites, int delay, int[] xOffsets, int[] yOffsets, boolean loop) {
        this.sprites = sprites;
        this.delay = delay;
        this.xOffsets = xOffsets;
        this.yOffsets = yOffsets;
        this.loop = loop;
    }

    public void draw(Graphics g, int x, int y, Direction direction, int imageUsed) {

        BufferedImage sprite = sprites[imageUsed];
        int xOffset = xOffsets[imageUsed];
        int yOffset = yOffsets[imageUsed];
        switch (direction) {

            case LEFT:
                //This is the default orientation. We simply draw the image to the given coordinates, after adding the offsets.
                g.drawImage(sprite, x + xOffset, y + yOffset, null);
                break;
            case RIGHT:
                //In this case, we have to reverse the image. Since we are only reversing along the x axis, the y coordinate remains unchanged.
                //For the x coordinate, we want the offset to be on the opposite side, which means that no offset is to be applied from x except the image's width.
                g.drawImage(sprite, x + sprite.getWidth(), y + yOffset, -sprite.getWidth(), sprite.getHeight(), null);
                break;
        }
    }

    public int getLength() {
        return sprites.length;
    }

    public BufferedImage[] getSprites() {
        return sprites;
    }

    public int getDelay() {
        return delay;
    }

    public int[] getXOffsets() {
        return xOffsets;
    }

    public int[] getYOffsets() {
        return yOffsets;
    }

    public boolean isLoop() {
        return loop;
    }
}
