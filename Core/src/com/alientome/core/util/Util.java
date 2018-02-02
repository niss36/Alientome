package com.alientome.core.util;

import com.alientome.core.Context;
import com.alientome.core.keybindings.InputListener;
import com.alientome.core.keybindings.MappedKeyEvent;
import com.alientome.core.vecmath.Vec2;
import com.jcabi.xml.XMLDocument;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {

    private static final Logger log = Logger.get();
    private static final SimpleDateFormat screenshotDateFormat = new SimpleDateFormat("YYYY-MM-dd_HH.mm.ss");

    /**
     * Not instantiable
     */
    private Util() {

    }

    /**
     * Function used to decrease velocity, that is, make it closer to zero.
     *
     * @param toDecrease the number to be decreased.
     * @param value      the amount to decrease.
     * @return If <code>toDecrease==0</code> 0 else <code>toDecrease</code> closer to 0 by <code>value</code>.
     */
    public static double decrease(double toDecrease, double value) {
        return Math.abs(toDecrease) - value <= 0 ? 0 : toDecrease < 0 ? toDecrease + value : toDecrease - value;
    }

    /**
     * Function used to decrease a vector representing velocity.
     *
     * @param vec   the <code>Vec2</code> to be decreased.
     * @param value the amount to decrease.
     */
    public static void decrease(Vec2 vec, double value) {

        vec.setX(decrease(vec.getX(), value));
        vec.setY(decrease(vec.getY(), value));
    }

    public static double clamp(double value, double minVal, double maxVal) {

        return value < minVal ? minVal : value > maxVal ? maxVal : value;
    }

    public static int clamp(int value, int minVal, int maxVal) {

        return value < minVal ? minVal : value > maxVal ? maxVal : value;
    }

    public static double lerp(double start, double end, double t) {
        return start + t * (end - start);
    }

    public static Vec2 lerpVec2(Vec2 start, Vec2 end, double t) {
        return new Vec2(lerp(start.getX(), end.getX(), t), lerp(start.getY(), end.getY(), t));
    }

    public static double diagonalDistance(Vec2 pos0, Vec2 pos1) {

        double dx = pos1.getX() - pos0.getX(), dy = pos1.getY() - pos0.getY();
        return Math.max(Math.abs(dx), Math.abs(dy));
    }

    public static InputListener makeListener(Runnable work) {

        return makeListener(work, mappedKeyEvent -> mappedKeyEvent.pressed);
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

    public static void saveScreenshot(Context context, BufferedImage image) {

        FileManager manager = context.getFileManager();

        Date date = new Date();
        String timestamp = screenshotDateFormat.format(date);

        File outputFile;
        String fileName = timestamp;

        for (int i = 1; (outputFile = manager.getScreenshot(fileName)).exists(); i++)
            fileName = timestamp + "_" + i;

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

    public static BufferedImage scale(BufferedImage source, int scale) {

        if (scale == 1)
            return source;

        assert scale > 1;

        int scaledWidth = source.getWidth() * scale; //The new width after scaling
        int scaledHeight = source.getHeight() * scale; //The new height after scaling

        BufferedImage image = new BufferedImage(scaledWidth, scaledHeight, source.getType()); //Create a blank image of scaled dimensions

        Graphics g = image.createGraphics();

        g.drawImage(source, 0, 0, scaledWidth, scaledHeight, null); //And draw the image to it

        return image;
    }

    public static double roundClosest(double toRound, double step) {

        if (step == 0) throw new IllegalArgumentException("Step cannot be 0");

        return Math.round(toRound / step) * step;
    }

    public static WrappedXML parseXMLNew(String documentPath) throws IOException {
        try (InputStream stream = ClassLoader.getSystemResourceAsStream(documentPath)) {
            return new WrappedXML(new XMLDocument(stream));
        }
    }

    public static WrappedXML parseXMLNew(URI uri) throws IOException {
        return new WrappedXML(new XMLDocument(uri));
    }

    public static Element parseXML(File file) throws IOException, SAXException, ParserConfigurationException {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(file).getDocumentElement();
    }

    public static double scale(double oldValue, double oldMin, double oldMax, double newMin, double newMax) {

        return (((oldValue - oldMin) * (newMax - newMin)) / (oldMax - oldMin)) + newMin;
    }

    public static File getParentFile(String path) {

        if (path == null || path.isEmpty())
            return null;

        return new File(path).getParentFile();
    }

    private static final Pattern dimensionPattern = Pattern.compile("\\s*([0-9]+)x([0-9]+)\\s*");

    public static Dimension parseDimension(String string) {

        Matcher matcher = dimensionPattern.matcher(string);

        if (matcher.find()) {

            int width = Integer.parseInt(matcher.group(1));
            int height = Integer.parseInt(matcher.group(2));

            return new Dimension(width, height);
        }

        throw new IllegalArgumentException("Invalid dimension : " + string);
    }
}