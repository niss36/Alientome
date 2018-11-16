package com.alientome.game.background;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Provides a few utility methods to optimise background images.
 */
public class BgUtil {

    /**
     * This method attempts to reduce the size of the source image by finding reduced dimensions, if possible, where
     * only completely transparent pixels are left out. It only examines the vertical axis, as it is assumed that
     * images passed as parameters here are used in the {@link ParallaxBackground} class, and thus loop around
     * horizontally; they shouldn't have empty columns.
     *
     * <p/>The returned rectangle has its x coordinate set to 0 and the same width as the original image, but its y coordinate and height are modified
     * according to the optimal top and bottom limits.
     *
     * <p/>One can then use {@link #crop(BufferedImage, Rectangle)} to get the actual optimal image. In order to draw it
     * properly, the draw y coordinate should be offset by the rectangle's y coordinate. For example:
     * {@code graphics.drawImage(optimisedImage, x, y + optimalRect.y, null)}
     *
     * @param source the image to optimise
     * @return the optimal rectangle
     */
    public static Rectangle findOptimalRect(BufferedImage source) {

        int topLimit = 0;
        int botLimit = source.getHeight() - 1;

        boolean foundTopLimit = false;

        while (!foundTopLimit) {

            if (isObstructed(source, topLimit)) {
                foundTopLimit = true;
            } else {
                topLimit++;
            }
        }

        boolean foundBotLimit = false;

        while (!foundBotLimit) {

            if (isObstructed(source, botLimit)) {
                foundBotLimit = true;
            } else {
                botLimit--;
            }
        }

        return new Rectangle(0, topLimit, source.getWidth(), botLimit - topLimit + 1);
    }

    private static boolean isObstructed(BufferedImage image, int y) {

        for (int x = 0; x < image.getWidth(); x++) {

            int rgb = image.getRGB(x, y);
            int alpha = (rgb >> 24) & 0xFF;
            if (alpha != 0)
                return true;
        }

        return false;
    }

    /**
     * Creates a new image by cropping the source to the given bounds. The source is not modified.
     *
     * <p/>Moreover, the source and returned images do not share their data; if the source is subsequently modified,
     * it will not affect the returned image. Also, if the source goes out of scope, its data will not remain in
     * memory.
     *
     * @param source the image to crop
     * @param bounds the crop bounds
     * @return a new image that corresponds to the source cropped by the bounds.
     */
    public static BufferedImage crop(BufferedImage source, Rectangle bounds) {
        BufferedImage image = new BufferedImage(bounds.width, bounds.height, source.getType());
        Graphics g = image.createGraphics();
        g.drawImage(source.getSubimage(bounds.x, bounds.y, bounds.width, bounds.height), 0, 0, null);
        g.dispose();
        return image;
    }
}
