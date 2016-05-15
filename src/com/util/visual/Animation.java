package com.util.visual;

import com.util.Direction;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Animation {

    private final BufferedImage[] sprites;

    private final int times;

    private int imageUsed = 0;
    private int count = 0;

    public Animation(BufferedImage[] sprites, int times) {
        this.sprites = sprites;
        this.times = times;
    }

    public void draw(Graphics g, int x, int y, Direction direction) {

        if(++ count >= times) {
            count = 0;
            imageUsed ++;
        }

        if(imageUsed >= sprites.length) imageUsed = 0;

        switch (direction) {

            case LEFT:
                g.drawImage(sprites[imageUsed], x, y, null);
                break;
            case RIGHT:
                g.drawImage(sprites[imageUsed], x + sprites[imageUsed].getWidth(), y, -sprites[imageUsed].getWidth(), sprites[imageUsed].getHeight(), null);
                break;
        }
    }

    public void reset() {
        imageUsed = 0;
        count = 0;
    }
}
