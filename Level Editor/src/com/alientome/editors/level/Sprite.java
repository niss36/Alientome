package com.alientome.editors.level;

import com.alientome.core.util.Direction;
import com.alientome.core.util.WrappedXML;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.ImageView;

import java.awt.*;
import java.awt.image.BufferedImage;

import static com.alientome.editors.level.util.Util.averageColor;

public class Sprite {

    public final ImageView view;
    public final Dimension dimension;
    public final Color color;
    private final BufferedImage image;
    private final int xOffset;
    private final int yOffset;

    public Sprite(BufferedImage image, int xOffset, int yOffset, Dimension dimension) {
        this.image = image;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.dimension = dimension;
        color = averageColor(image);

        view = new ImageView(SwingFXUtils.toFXImage(image, null));
    }

    public Sprite(Color color, Dimension dimension) {

        this.color = color;
        this.dimension = dimension;
        xOffset = 0;
        yOffset = 0;

        image = new BufferedImage(dimension.width, dimension.height, BufferedImage.TYPE_INT_ARGB);

        Graphics graphics = image.createGraphics();
        graphics.setColor(color);
        graphics.fillRect(0, 0, dimension.width, dimension.height);

        view = new ImageView(SwingFXUtils.toFXImage(image, null));
    }

    public static Sprite fromXML(WrappedXML visualXML, Dimension defaultDimension) {

        String type = visualXML.getAttr("type");
        String value = visualXML.getAttr("value");

        switch (type) {

            case "color":
                int argb = Long.decode(value).intValue();
                Color color = new Color(argb, true);
                return new Sprite(color, defaultDimension);

            case "sprite":
                return SpritesLoader.get(value);

            default:
                throw new IllegalArgumentException("Unknown visual type : " + type);
        }
    }

    public void draw(Graphics g, int x, int y, Direction direction, Color boxColor, boolean drawBox) {

        switch (direction) {

            case LEFT:
                g.drawImage(image, x + xOffset, y + yOffset, null);
                break;

            case RIGHT:
                g.drawImage(image, x  + image.getWidth(), y + yOffset, -image.getWidth(), image.getHeight(), null);
        }

        if (drawBox) {
            g.setColor(boxColor);
            g.drawRect(x, y, dimension.width, dimension.height);
        }
    }

    public void draw(Graphics g, int x, int y, Color boxColor, boolean drawBox) {

        draw(g, x, y, Direction.LEFT, boxColor, drawBox);
    }
}
