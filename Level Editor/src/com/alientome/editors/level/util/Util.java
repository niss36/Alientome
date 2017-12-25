package com.alientome.editors.level.util;

import javafx.application.Platform;
import org.w3c.dom.Document;

import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class Util {

    public static Color averageColor(BufferedImage image) {

        int width = image.getWidth();
        int height = image.getHeight();

        int redCount = 0;
        int greenCount = 0;
        int blueCount = 0;
        int alphaCount = 0;

        int pxCount = width * height;

        int[] rgb = image.getRGB(0, 0, width, height, null, 0, width);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int col = rgb[x + y * width];
                redCount += (col >> 16) & 0xFF;
                greenCount += (col >> 8) & 0xFF;
                blueCount += col & 0xFF;
                alphaCount += (col >> 24) & 0xFF;
            }
        }

        return new Color(redCount / pxCount, greenCount / pxCount, blueCount / pxCount, alphaCount / pxCount);
    }

    public static <T> T[][] deepCopy(T[][] source, T[][] target) {

        for (int i = 0; i < source.length; i++)
            System.arraycopy(source[i], 0, target[i], 0, source[i].length);

        return target;
    }

    public static <T extends Copyable<T>> List<T> deepCopy(List<T> source) {

        List<T> target = new ArrayList<>(source.size());
        for (T t : source)
            target.add(t.copy());

        return target;
    }

    public static <T> void update(List<T> target, List<T> newState) {

        int i = 0;

        for (; i < newState.size(); i++)
            if (i < target.size())
                target.set(i, newState.get(i));
            else
                target.add(newState.get(i));
        for (; i < target.size(); i++)
            target.remove(i);
    }

    public static boolean checkArrayBounds(int i, int length) {
        return i >= 0 && i < length;
    }

    public static void saveStandardised(Document document, Result target) throws TransformerException {

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

        DOMSource source = new DOMSource(document);
        transformer.transform(source, target);
    }

    public static void invokeAndWait(Runnable task) {

        if (Platform.isFxApplicationThread()) {
            task.run();
            return;
        }

        FutureTask<Void> future = new FutureTask<>(() -> {
            task.run();
            return null;
        });

        Platform.runLater(future);

        try {
            future.get();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
