package com.util.visual;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import static com.util.Util.log;

/**
 * This static class contains the logic to load and store sprites.
 */
public final class SpritesLoader {

    private static final HashMap<Class<?>, Animation[]> animations = new HashMap<>();

    private static boolean loaded = false;

    /**
     * Not instantiable
     */
    private SpritesLoader() {
    }

    /**
     * Used when initializing the textures.
     *
     * @param path the path to the sprite in the resources folder
     * @return the <code>BufferedImage</code> at this location, or <code>null</code> if it doesn't exist.
     */
    public static BufferedImage getSprite(String path) {

        BufferedImage sprite = null;

        try (InputStream stream = ClassLoader.getSystemResourceAsStream(path + ".png")) {
            sprite = ImageIO.read(stream);
        } catch (IOException | IllegalArgumentException e) {
            e.printStackTrace();
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
            sprites[i] = getSprite(dirPath + i);
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
            log("Loading animations", 0);
            long start = System.nanoTime();

            try {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();

                Document document = builder.parse(ClassLoader.getSystemResourceAsStream("animations.xml"));

                Element root = document.getDocumentElement();

                NodeList packages = root.getElementsByTagName("package");
                for (int i = 0; i < packages.getLength(); i++) {
                    Element packageNode = (Element) packages.item(i);

                    String packageName = packageNode.getAttribute("name");
                    String packageDirectory = packageNode.getAttribute("directory");

                    NodeList classes = packageNode.getElementsByTagName("class");

                    for (int j = 0; j < classes.getLength(); j++) {
                        Element classNode = (Element) classes.item(j);

                        String className = packageName + classNode.getAttribute("name");
                        String classDirectory = packageDirectory + classNode.getAttribute("subDirectory");

                        NodeList animations = classNode.getElementsByTagName("animation");

                        AnimationInfo[] info = new AnimationInfo[animations.getLength()];

                        for (int k = 0; k < animations.getLength(); k++) {
                            Element animationNode = (Element) animations.item(k);

                            String animationPath = classDirectory + animationNode.getAttribute("path");

                            info[k] = AnimationInfo.parse(animationPath, animationNode);
                        }

                        try {
                            init(Class.forName(className), info);
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } catch (ParserConfigurationException | SAXException | IOException e) {
                e.printStackTrace();
            }

            loaded = true;

            long elapsed = (System.nanoTime() - start) / 1_000_000;
            log("Loaded animations in " + elapsed + "ms", 0);
        }, "Thread-AnimationLoad").start();
    }

    public static boolean hasLoaded() {
        return loaded;
    }
}
