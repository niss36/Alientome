package com.util.visual;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

/**
 * This static class contains the logic to load and store sprites.
 */
public class SpritesLoader {

    /**
     * Not instantiable
     */
    private SpritesLoader() {
    }

    /**
     * Gets a .png image from the specified path, relative to system resources root
     *
     * @param path the path to the image, in the resources folder
     * @return the <code>BufferedImage</code> read, or null if an <code>IOException</code> occurs
     * (Typically, if the image doesn't exist)
     */
    public static BufferedImage readImage(String path) {

        BufferedImage image = null;

        try (InputStream stream = ClassLoader.getSystemResourceAsStream(path)) {
            image = ImageIO.read(stream);
        } catch (IOException | IllegalArgumentException e) {
            System.err.println("Could not read image at path '" + path + "' : ");
            e.printStackTrace();
        }

        return image;
    }
}
