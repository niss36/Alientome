package com.util.visual;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;

/**
 * This static class contains the logic to load and store sprites.
 */
public class SpritesLoader {

    public static final BufferedImage NULL = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);

    private static final HashMap<Class, Animation[]> animationsMap = new HashMap<>();

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
    private static BufferedImage[] getSpritesAnimated(String dirPath, int spritesCount) {

        BufferedImage[] sprites = new BufferedImage[spritesCount];

        for (int i = 0; i < spritesCount; i++) {
            sprites[i] = getSprite(dirPath + "/" + i);
        }

        return sprites;
    }

    /**
     * Initializes and stores the <code>caller</code>'s <code>Animation</code>s
     *
     * @param caller the <code>Class</code> to initialize
     * @param info   the <code>AnimationInfo</code> array, one element equals one <code>Animation</code>
     */
    public static void init(Class caller, AnimationInfo[] info) {

        if (!animationsMap.containsKey(caller)) {

            Animation[] animations = new Animation[info.length];

            for (int i = 0; i < info.length; i++) {

                BufferedImage[] sprites = getSpritesAnimated(info[i].directory, info[i].spritesCount);

                animations[i] = new Animation(sprites, info[i].times);
            }

            animationsMap.put(caller, animations);
        }
    }

    /**
     * @param caller the <code>Class</code> to retrieve the <code>Animation</code>s of
     * @return an array of <code>Animation</code>s that was previously stored
     */
    public static Animation[] getAnimation(Class caller) {

        return animationsMap.get(caller);
    }
}
