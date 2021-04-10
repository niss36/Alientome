package com.alientome.core.util;

import com.alientome.core.keybindings.InputListener;
import com.alientome.core.keybindings.MappedKeyEvent;
import com.jcabi.xml.XMLDocument;
import javafx.beans.property.Property;
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

    public static File findScreenshotFile(FileManager manager, String timestamp) {

        File outputFile;
        String fileName = timestamp;

        for (int i = 1; (outputFile = manager.getScreenshot(fileName)).exists(); i++)
            fileName = timestamp + "_" + i;

        return outputFile;
    }

    public static void saveScreenshot(FileManager manager, BufferedImage image) {

        Date date = new Date();
        String timestamp = screenshotDateFormat.format(date);

        File outputFile = findScreenshotFile(manager, timestamp);

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

    public static <T> T require(Property<T> p) {
        T v = p.getValue();
        if (v == null)
            throw new IllegalStateException("Missing component : " + p.getName());
        return v;
    }
}