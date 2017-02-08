package com.util.visual;

import com.util.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.imageio.ImageIO;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import static com.util.Util.parseXML;

/**
 * This static class contains the logic to load and store sprites.
 */
public class SpritesLoader {

    private static final HashMap<Class<?>, Animation[]> animations = new HashMap<>();
    private static final Logger log = Logger.get();
    private static final Object loadLock = new Object();
    private static boolean loaded = false;

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

        try (InputStream stream = ClassLoader.getSystemResourceAsStream(path + ".png")) {
            image = ImageIO.read(stream);
        } catch (IOException | IllegalArgumentException e) {
            System.err.println("Could not read image at path '" + path + ".png' : ");
            e.printStackTrace();
        }

        return image;
    }

    /**
     * Used when initializing the textures.
     *
     * @param path the path to the sprite in the resources folder
     * @return the <code>BufferedImage</code> at this location, or <code>null</code> if it doesn't exist.
     */
    public static BufferedImage getSprite(String path) {

        BufferedImage sprite = readImage(path);

        if (path.contains("Entity") && sprite != null) {
            int newWidth = sprite.getWidth() * 2;
            int newHeight = sprite.getHeight() * 2;
            if (newWidth > 0 && newHeight > 0) {
                BufferedImage newSprite = new BufferedImage(newWidth, newHeight, sprite.getType());
                Graphics g = newSprite.createGraphics();
                g.drawImage(sprite, 0, 0, newWidth, newHeight, null);
                sprite = newSprite;
            }
        }

        return sprite;
    }

    /**
     * Used when initializing animated textures.
     *
     * @param dirPath      the path to the directory where the sprites are located
     * @param spritesCount the number of sprites
     * @return a <code>BufferedImage[]</code> containing the sprites found
     * @see #getSprite(String)
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
     * @see #getAnimations(Class)
     */
    private static void init(Class<?> caller, AnimationInfo[] info) {

        Animation[] animations = null;

        if (info.length > 0) {

            animations = new Animation[info.length];

            for (int i = 0; i < info.length; i++) {

                BufferedImage[] sprites = getSpritesAnimated(info[i].directory, info[i].spritesCount);

                animations[i] = new Animation(sprites, info[i]);
            }
        }

        SpritesLoader.animations.put(caller, animations);
    }

    /**
     * @param caller the <code>Class</code> to retrieve the <code>Animation</code>s of
     * @return an array of <code>Animation</code>s that was previously stored, or
     * <code>null</code> if no element was stored for the <code>caller Class</code>
     * @see #init(Class, AnimationInfo[])
     */
    static Animation[] getAnimations(Class<?> caller) {

        return animations.get(caller);
    }

    public static void load() {

        new Thread(() -> {
            log.i("Loading animations");
            long start = System.nanoTime();

            try {

                Element root = parseXML("animations");

                NodeList packages = root.getElementsByTagName("package");
                for (int i = 0; i < packages.getLength(); i++) {
                    Element packageNode = (Element) packages.item(i);

                    String packageName = packageNode.getAttribute("name");
                    String packageDirectory = "Sprites/" + packageNode.getAttribute("directory");

                    NodeList classes = packageNode.getElementsByTagName("class");

                    for (int j = 0; j < classes.getLength(); j++) {
                        Element classNode = (Element) classes.item(j);

                        String className = packageName + "." + classNode.getAttribute("name");
                        String classDirectory = packageDirectory + "/" + classNode.getAttribute("subDirectory");

                        NodeList animations = classNode.getElementsByTagName("animation");

                        AnimationInfo[] info = new AnimationInfo[animations.getLength()];

                        for (int k = 0; k < animations.getLength(); k++) {
                            Element animationNode = (Element) animations.item(k);

                            String animationRelPath = animationNode.getAttribute("path");
                            if (!animationRelPath.isEmpty())
                                animationRelPath = "/" + animationRelPath;
                            String animationPath = classDirectory + animationRelPath;

                            info[k] = AnimationInfo.parse(animationPath, animationNode);
                        }

                        try {
                            init(Class.forName(className), info);
                        } catch (ClassNotFoundException e) {
                            System.err.println("Class '" + className + "' could not be found :");
                            e.printStackTrace();
                        }
                    }
                }
            } catch (ParserConfigurationException | SAXException | IOException e) {
                e.printStackTrace();
            }

            synchronized (loadLock) {
                loaded = true;
                loadLock.notify();
            }

            long elapsed = (System.nanoTime() - start) / 1_000_000;
            log.i("Loaded animations in " + elapsed + "ms");
        }, "Thread-AnimationLoad").start();
    }

    public static void waitUntilLoaded() throws InterruptedException {

        synchronized (loadLock) {
            if (!loaded)
                loadLock.wait();
        }
    }
}
