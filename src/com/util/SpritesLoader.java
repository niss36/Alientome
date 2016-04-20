package com.util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class SpritesLoader {

    public static final BufferedImage NULL = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);

    /**
     * Used when initializing the textures.
     *
     * @param path the path to the sprite in the resources folder
     * @return the <code>BufferedImage</code> at this location, or <code>SpritesLoader.NULL</code> if it doesn't exist.
     */
    public static BufferedImage getSprite(String path) {

        BufferedImage sprite = null;

        try {
            sprite = ImageIO.read(ClassLoader.getSystemResourceAsStream(path + ".png"));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            return NULL;
        }

        return sprite;
    }

    /**
     * Used when initializing animated textures.
     *
     * @param dirPath      the path to the directory where the sprites are located
     * @param spritesCount the number of sprites
     * @return a <code>BufferedImage[]</code> containing the sprites found
     * @see SpritesLoader#getSprite
     */
    public static BufferedImage[] getSpritesAnimated(String dirPath, int spritesCount) {

        BufferedImage[] sprites = new BufferedImage[spritesCount];

        for (int i = 0; i < spritesCount; i++) {
            sprites[i] = getSprite(dirPath + "/" + i);
        }

        return sprites;
    }
}
