package com.util.visual;

import com.util.Direction;

import java.awt.image.BufferedImage;

class Animation {

    private final BufferedImage[] sprites;
    private final int delay;
    private final int[] offsetsX;
    private final int[] offsetsY;
    private final boolean loop;

    Animation(BufferedImage[] sprites, AnimationInfo info) {
        this.sprites = sprites;
        delay = info.delay;
        offsetsX = info.offsetsX;
        offsetsY = info.offsetsY;
        loop = info.loop;
    }

    public void draw(GameGraphics g, int x, int y, Direction direction, int imageUsed) {

        switch (direction) {

            case LEFT:
                g.graphics.drawImage(sprites[imageUsed], x + offsetsX[imageUsed], y + offsetsY[imageUsed], null);
                break;
            case RIGHT:
                g.graphics.drawImage(sprites[imageUsed], x + sprites[imageUsed].getWidth(), y + offsetsY[imageUsed], -sprites[imageUsed].getWidth(), sprites[imageUsed].getHeight(), null);
                break;
        }
    }

    int getDelay() {
        return delay;
    }

    int getLength() {
        return sprites.length;
    }

    boolean isLoop() {
        return loop;
    }
}
