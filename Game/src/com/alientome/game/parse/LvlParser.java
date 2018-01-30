package com.alientome.game.parse;

import com.alientome.core.collisions.AxisAlignedBoundingBox;
import com.alientome.core.util.ArrayCreator;
import com.alientome.core.util.WrappedXML;
import com.alientome.game.collisions.StaticBoundingBox;
import com.jcabi.xml.XML;
import org.w3c.dom.Element;

import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class LvlParser {

    public static <BG, LAYER> BG parseBackground(WrappedXML document,
                                                 ImageParser imageParser,
                                                 XMLLayerParser<LAYER> layerParser,
                                                 XMLBackgroundParser<BG, LAYER> backgroundParser) {

        WrappedXML backgroundXML = document.getFirst("level/background");

        int yOffset = backgroundXML.getOrDefaultInt("yOffset", 0);
        int scale = backgroundXML.getOrDefaultInt("scale", 1);

        List<XML> layersXML = backgroundXML.nodes("layer");
        List<LAYER> layers = new ArrayList<>(layersXML.size());

        for (XML layerXML : layersXML) {
            WrappedXML wrappedXML = new WrappedXML(layerXML);

            double xCoef = wrappedXML.getOrDefaultDouble("xCoef", 0);
            double yCoef = wrappedXML.getOrDefaultDouble("yCoef", 0);

            String src = wrappedXML.getAttr("source");
            BufferedImage image = imageParser.parse(src, scale);

            layers.add(layerParser.parse(xCoef, yCoef, src, image));
        }

        return backgroundParser.parse(layers, yOffset, scale);
    }

    public static void parseEntitiesXML(WrappedXML document, XMLEntityParser callback) {

        for (WrappedXML entityXML : document.nodesWrapped("entities/entity")) {

            String id = entityXML.getAttr("id");
            int x = entityXML.getAttrInt("x");
            int y = entityXML.getAttrInt("y");

            Map<String, String> tags = new LinkedHashMap<>();

            for (XML tagXML : entityXML.nodes("tags/*")) {

                Element tag = (Element) tagXML.node();
                tags.put(tag.getTagName(), tag.getTextContent());
            }

            callback.parse(id, x, y, tags);
        }
    }

    public static <T> T[] parseBlocksDictionaryXML(WrappedXML document, XMLBlockStateParser<T> callback, ArrayCreator<T> creator) {

        List<XML> statesXML = document.nodes("dictionary/blocks/state");
        T[] dictionary = creator.create(statesXML.size());

        for (int i = 0; i < dictionary.length; i++) {

            WrappedXML wrappedXML = new WrappedXML(statesXML.get(i));
            dictionary[i] = callback.parse(wrappedXML.getAttr("id"), wrappedXML.getAttr("meta"));
        }

        return dictionary;
    }

    public static void parseScriptsXML(WrappedXML document, XMLScriptParser callback) {

        Set<String> ids = new HashSet<>();

        for (WrappedXML script : document.nodesWrapped("scripts/scriptObject")) {

            String id = script.getOrDefault("id", null);

            if (id != null && ! ids.add(id))
                throw new IllegalArgumentException("ids must be unique (offending : " + id + ")");

            boolean enabled = script.getOrDefaultBoolean("enabled", true);

            WrappedXML boundingBox = script.getFirst("boundingBox");

            double x = boundingBox.getAttrDouble("x");
            double y = boundingBox.getAttrDouble("y");
            double w = boundingBox.getAttrDouble("w");
            double h = boundingBox.getAttrDouble("h");

            AxisAlignedBoundingBox aabb = new StaticBoundingBox(x, y, x + w, y + h);

            WrappedXML affected = script.getFirst("affected");

            String content = script.xpath("content/text()").get(0);

            callback.parse(id, enabled, aabb, affected.getAttr("class"), content);
        }
    }

    public static void parseMap(InputStream input, int width, int height, MapBlockParser parser) throws IOException {

        try (DataInputStream stream = new DataInputStream(input)) {
            for (int y = 0; y < height; y++)
                for (int x = 0; x < width; x++)
                    parser.parse(x, y, stream.readUnsignedByte());
        }
    }
}
