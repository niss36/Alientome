package com.alientome.visual.sheets;

import com.alientome.core.util.Direction;
import com.alientome.core.util.Util;
import com.alientome.visual.Visual;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * A sprite sheet is an array of images, extracted from a single source image.
 * All images share the same dimension.
 */
public class SpriteSheet implements Visual {

    /**
     * The array of images of the sprite sheet
     */
    private final BufferedImage[] sprites;

    public SpriteSheet(BufferedImage source, Dimension dimension, int cols, int num, int scale) {

        sprites = new BufferedImage[num];

        for (int i = 0; i < num; i++) {

            int x = i % cols;
            int y = i / cols;

            int srcX = x * dimension.width + x;
            int srcY = y * dimension.height + y;

            BufferedImage sub = source.getSubimage(srcX, srcY, dimension.width, dimension.height);

            sprites[i] = Util.scale(sub, scale);
        }
    }

    public void draw(Graphics g, int x, int y, Direction direction, int spriteUsed) {

        BufferedImage sprite = sprites[spriteUsed];

        switch (direction) {

            case LEFT:
                g.drawImage(sprite, x, y, null);
                break;

            case RIGHT:
                g.drawImage(sprite, x + sprite.getWidth(), y, -sprite.getWidth(), sprite.getHeight(), null);
                break;
        }
    }
}
