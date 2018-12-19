package com.alientome.editors.level;

import com.alientome.core.util.Util;
import com.alientome.core.util.WrappedXML;
import com.jcabi.xml.XML;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.alientome.core.util.Util.parseXMLNew;

public class SpritesLoader {

    private static final Map<String, Sprite[]> sprites = new HashMap<>();

    public static Sprite get(String id) {

        int separatorIndex = id.lastIndexOf(':');

        String classFullName = id.substring(0, separatorIndex);
        int index = Integer.parseInt(id.substring(separatorIndex + 1));

        return get(classFullName, index);
    }

    public static Sprite get(String classFullName, int index) {

        return sprites.get(classFullName)[index];
    }

    private static Sprite parse(String path, Dimension dimension, int scale) throws IOException {

        try (DataInputStream stream = new DataInputStream(new BufferedInputStream(ClassLoader.getSystemResourceAsStream(path)))) {

            stream.readInt(); //Skip length
            stream.readInt(); //Skip delay
            stream.readBoolean(); //Skip loop

            int xOffset = 0;
            int yOffset = 0;

            boolean xOffsetsUniform = stream.readBoolean();
            if (xOffsetsUniform)
                xOffset = stream.readInt() * scale;

            boolean yOffsetsUniform = stream.readBoolean();
            if (yOffsetsUniform)
                yOffset = stream.readInt() * scale;

            BufferedImage image = ImageIO.read(stream);

            if (scale > 1)
                image = Util.scale(image, scale);

            //noinspection StatementWithEmptyBody
            while (stream.readByte() != (byte) 0xFF);

            if (!xOffsetsUniform)
                xOffset = stream.readInt() * scale;
            if (!yOffsetsUniform)
                yOffset = stream.readInt() * scale;

            return new Sprite(image, xOffset, yOffset, dimension);

        }
    }

    public static void load() throws IOException {

        WrappedXML xml = parseXMLNew("animations.xml");

        for (WrappedXML packageXML : xml.nodesWrapped("animations/package")) {

            String packageName = packageXML.getAttr("name");
            String packageDirectory = "Sprites/" + packageXML.getAttr("directory");

            for (WrappedXML classXML : packageXML.nodesWrapped("class")) {

                String className = packageName + "." + classXML.getAttr("name");
                String classDirectory = packageDirectory + "/" + classXML.getAttr("subDirectory");

                List<XML> animations = classXML.nodes("animation");
                Sprite[] sprites = new Sprite[animations.size()];

                for (int i = 0; i < animations.size(); i++) {

                    WrappedXML animationXML = new WrappedXML(animations.get(i));

                    String path = classDirectory + "/" + animationXML.getAttr("name");
                    int scale = animationXML.getOrDefaultInt("scale", 1);
                    Dimension dimension = animationXML.getAttrAs("dimension", Util::parseDimension);
                    dimension.width *= scale;
                    dimension.height *= scale;

                    sprites[i] = parse(path, dimension, scale);
                }

                SpritesLoader.sprites.put(className, sprites);
            }
        }
    }
}
