package com.util;

import com.keybindings.MappedKeyEvent;
import com.util.listeners.InputListener;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.Predicate;

public class Util {

    private static final Logger log = Logger.get();
    private static final SimpleDateFormat screenshotDateFormat = new SimpleDateFormat("YYYY-MM-dd_HH.mm.ss");

    /**
     * Not instantiable
     */
    private Util() {

    }

    /**
     * Method used to decrease velocity, that is, make it closer to zero.
     *
     * @param toDecrease the number to be decreased.
     * @param value      the amount to decrease.
     * @return If <code>toDecrease==0</code> 0 else <code>toDecrease</code> closer to 0 by <code>value</code>.
     */
    public static double decrease(double toDecrease, double value) {
        return Math.abs(toDecrease) - value <= 0 ? 0 : toDecrease < 0 ? toDecrease + value : toDecrease - value;
    }

    /**
     * Method used to decrease a vector representing velocity.
     *
     * @param vec   the <code>Vec2</code> to be decreased.
     * @param value the amount to decrease.
     */
    public static void decrease(Vec2 vec, double value) {

        vec.x = decrease(vec.x, value);
        vec.y = decrease(vec.y, value);
    }

    public static int center(double length1, double length2) {

        return (int) (length1 / 2 - length2 / 2);
    }

    public static double clamp(double value, double minVal, double maxVal) {

        return value < minVal ? minVal : value > maxVal ? maxVal : value;
    }

    public static int clamp(int value, int minVal, int maxVal) {

        return value < minVal ? minVal : value > maxVal ? maxVal : value;
    }

    public static String keycodeToString(int keyCode) {

        if (keyCode <= 0) return "NONE";

        for (Field f : KeyEvent.class.getFields()) {

            if (f.getName().startsWith("VK_")) {
                try {
                    if ((Integer) f.get(null) == keyCode) {
                        return f.getName().substring(3);
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

    public static InputListener makeListener(Runnable work) {

        return makeListener(work, MappedKeyEvent::isKeyPressed);
    }

    public static InputListener makeListener(Runnable work, Predicate<MappedKeyEvent> keyEventPredicate) {

        return e -> {
            if (keyEventPredicate.test(e)) {
                work.run();
                return true;
            }
            return false;
        };
    }

    public static void saveScreenshot(BufferedImage image) {

        Date date = new Date();
        String timestamp = screenshotDateFormat.format(date);

        File outputFile;
        String fileName = timestamp;
        int i = 1;

        while ((outputFile = FileManager.getInstance().getScreenshot(fileName)).exists()) {

            fileName = timestamp + "_" + i;
            i++;
        }

        try {
            ImageIO.write(image, "png", outputFile);
            log.i("Saved screenshot as " + outputFile.getPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static BufferedImage deepCopy(BufferedImage toCopy) {

        ColorModel cm = toCopy.getColorModel();
        WritableRaster raster = toCopy.copyData(null);
        boolean isAlphaPremultiplied = toCopy.isAlphaPremultiplied();

        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }

    public static double roundClosest(double toRound, double step) {

        if (toRound < 0) throw new IllegalArgumentException("Must round a positive number");

        double toRoundExceed = toRound % step;
        if (toRoundExceed == 0) return toRound;

        double stepDifference = step - toRoundExceed;
        if (stepDifference > toRoundExceed) return toRound - toRoundExceed;
        else return toRound + stepDifference;
    }

    /**
     * @param documentPath the path to the document, not ending in xml, relative to system resources root
     * @return the root <code>Element</code> of the parsed <code>Document</code>
     * @throws IOException                  if any I/O exception occurs
     * @throws SAXException                 if any parsing exception occurs
     * @throws ParserConfigurationException if the requested parser document type is not available
     */
    public static Element parseXML(String documentPath) throws IOException, SAXException, ParserConfigurationException {

        return parseXML(ClassLoader.getSystemResourceAsStream(documentPath + ".xml"));
    }

    public static Element parseXML(InputStream documentInputStream) throws IOException, SAXException, ParserConfigurationException {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(documentInputStream).getDocumentElement();
    }

    public static Element parseXML(File file) throws IOException, SAXException, ParserConfigurationException {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(file).getDocumentElement();
    }

    public static double scale(double oldValue, double oldMin, double oldMax, double newMin, double newMax) {

        return (((oldValue - oldMin) * (newMax - newMin)) / (oldMax - oldMin)) + newMin;
    }

    public static boolean isSelector(String arg) {

        return arg.charAt(0) == '@';
    }
}
