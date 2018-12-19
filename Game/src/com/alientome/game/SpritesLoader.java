package com.alientome.game;

import com.alientome.core.util.Logger;
import com.alientome.core.util.Util;
import com.alientome.core.util.WrappedXML;
import com.alientome.visual.Visual;
import com.alientome.visual.animations.Animation;
import com.alientome.visual.animations.AnimationsHandler;
import com.alientome.visual.animations.io.AnimationReader;
import com.alientome.visual.sheets.SheetsHandler;
import com.alientome.visual.sheets.SpriteSheet;
import com.jcabi.xml.XML;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.alientome.core.util.Util.parseXMLNew;

public class SpritesLoader {

    private static final Logger log = Logger.get();
    private static final Map<Class<?>, Visual[]> visuals = new HashMap<>();
    private static final List<String> toLoad = new ArrayList<>();
    private static final Object loadLock = new Object();
    private static boolean loaded = false;

    private SpritesLoader() {
    }

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

    public static BufferedImage readImage(String path, int scale) {

        if (scale < 1)
            throw new IllegalArgumentException("Illegal scale argument (" + scale + ") : Cannot be < 1");

        BufferedImage image = readImage(path);

        if (image != null && scale > 1)
            image = Util.scale(image, scale);

        return image;
    }

    private static void initAnimations(Class<?> clazz, String[] paths, int[] scales) {

        assert paths.length > 0;

        Animation[] animations = new Animation[paths.length];

        for (int i = 0; i < paths.length; i++) {

            InputStream stream = ClassLoader.getSystemResourceAsStream(paths[i]);
            if (stream != null)
                try (AnimationReader reader = new AnimationReader(stream)) {

                    animations[i] = reader.readAnimation(scales[i]);

                } catch (IOException e) {
                    e.printStackTrace();
                }
        }

        SpritesLoader.visuals.put(clazz, animations);
    }

    private static void initSheets(Class<?> clazz, String[] paths, Dimension[] dimensions, int[] cols, int[] nums, int[] scales) {

        assert paths.length > 0;

        SpriteSheet[] sheets = new SpriteSheet[paths.length];

        for (int i = 0; i < paths.length; i++) {

            try (InputStream stream = ClassLoader.getSystemResourceAsStream(paths[i])) {

                BufferedImage source = ImageIO.read(stream);
                sheets[i] = new SpriteSheet(source, dimensions[i], cols[i], nums[i], scales[i]);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        visuals.put(clazz, sheets);
    }

    public static AnimationsHandler newAnimationsHandlerFor(Class<?> clazz) {

        Animation[] animations = (Animation[]) visuals.get(clazz);

        return new AnimationsHandler(animations);
    }

    public static SheetsHandler newSheetsHandlerFor(Class<?> clazz) {

        SpriteSheet[] sheets = (SpriteSheet[]) visuals.get(clazz);

        return new SheetsHandler(sheets);
    }

    public static void register(String docPath) {
        toLoad.add(docPath);
    }

    public static void loadAll() {

        if (loaded)
            throw new IllegalStateException("Already loaded !");

        new Thread(() -> {

            try {
                for (String docPath : toLoad)
                    load(docPath);

                synchronized (loadLock) {
                    loaded = true;
                    loadLock.notify();
                }
            } catch (IOException | RuntimeException e) {
                log.e("Error while loading sprites :");
                e.printStackTrace();
                System.exit(-1);
            }

        }, "Thread-AnimationsLoad").start();
    }

    private static void load(String docPath) throws IOException {

        log.i("Loading " + docPath);
        long start = System.nanoTime();

        WrappedXML xml = parseXMLNew(docPath);

        for (WrappedXML packageXML : xml.nodesWrapped("animations/package")) {

            String packageName = packageXML.getAttr("name");
            String packageDirectory = "Sprites/" + packageXML.getAttr("directory");

            for (WrappedXML classXML : packageXML.nodesWrapped("class")) {

                String className = packageName + "." + classXML.getAttr("name");
                String classDirectory = packageDirectory + "/" + classXML.getAttr("subDirectory");

                List<XML> animations = classXML.nodes("animation");
                List<XML> sheets = classXML.nodes("sheet");

                try {
                    Class<?> clazz = Class.forName(className);

                    int animationsLength = animations.size();
                    int sheetsLength = sheets.size();

                    if (animationsLength > 0 && sheetsLength == 0) {

                        String[] paths = new String[animationsLength];
                        int[] scales = new int[paths.length];

                        for (int i = 0; i < animationsLength; i++) {
                            WrappedXML animationXML = new WrappedXML(animations.get(i));

                            paths[i] = classDirectory + "/" + animationXML.getAttr("name");
                            scales[i] = animationXML.getOrDefaultInt("scale", 1);
                        }

                        initAnimations(clazz, paths, scales);

                    } else if (sheetsLength > 0 && animationsLength == 0) {

                        String[] paths = new String[sheetsLength];
                        Dimension[] dimensions = new Dimension[paths.length];
                        int[] cols = new int[paths.length];
                        int[] nums = new int[paths.length];
                        int[] scales = new int[paths.length];

                        for (int i = 0; i < sheetsLength; i++) {
                            WrappedXML sheetXML = new WrappedXML(sheets.get(i));

                            paths[i] = classDirectory + "/" + sheetXML.getAttr("name");
                            dimensions[i] = sheetXML.getAttrAs("dimension", Util::parseDimension);
                            cols[i] = sheetXML.getAttrInt("cols");
                            nums[i] = sheetXML.getAttrInt("num");
                            scales[i] = sheetXML.getOrDefaultInt("scale", 1);
                        }

                        initSheets(clazz, paths, dimensions, cols, nums, scales);

                    } else if (animationsLength > 0 && sheetsLength > 0)
                        log.w("Class '" + className + "' has both animations and sheets specified. Not loading anything.");

                } catch (ClassNotFoundException e) {
                    log.e("Class '" + className + "' could not be found :");
                    e.printStackTrace();
                }
            }
        }

        long elapsed = (System.nanoTime() - start) / 1_000_000;
        log.i("Loaded " + docPath + " in " + elapsed + "ms");
    }

    public static void waitUntilLoaded() throws InterruptedException {

        synchronized (loadLock) {
            while (!loaded)
                loadLock.wait();
        }
    }
}
