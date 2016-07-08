package com.util.visual;

import com.util.Direction;

import java.awt.*;
import java.awt.image.BufferedImage;

class Animation {

    private final BufferedImage[] sprites;
    private final int delay;
    private final int[] offsetsX;
    private final int[] offsetsY;
    private final boolean loop;

    private int imageUsed = 0;
    private int count = 0;

    Animation(BufferedImage[] sprites, AnimationInfo info) {
        this.sprites = sprites;
        delay = info.delay;
        offsetsX = info.offsetsX;
        offsetsY = info.offsetsY;
        loop = info.loop;
    }

    public void draw(Graphics g, int x, int y, Direction direction) {

        if (++count >= delay) {
            count = 0;
            imageUsed++;
        }

        if (imageUsed >= sprites.length) {
            if (loop) imageUsed = 0;
            else imageUsed = sprites.length - 1;
        }

        switch (direction) {

            case LEFT:
                g.drawImage(sprites[imageUsed], x + offsetsX[imageUsed], y + offsetsY[imageUsed], null);
                break;
            case RIGHT:
                g.drawImage(sprites[imageUsed], x + sprites[imageUsed].getWidth(), y + offsetsY[imageUsed], -sprites[imageUsed].getWidth(), sprites[imageUsed].getHeight(), null);
                break;
        }
    }

    public void draw(Graphics g, int x, int y) {
        draw(g, x, y, Direction.LEFT);
    }

    public void reset() {
        imageUsed = 0;
        count = 0;
    }
}
