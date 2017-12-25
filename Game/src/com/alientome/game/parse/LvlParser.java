package com.alientome.game.parse;

import com.alientome.core.collisions.AxisAlignedBoundingBox;
import com.alientome.core.util.ArrayCreator;
import com.alientome.game.collisions.StaticBoundingBox;
import com.jcabi.xml.XML;
import org.w3c.dom.Element;

import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class LvlParser {

    private static String getOrDefault(Element e, String name, String defaultVal) {
        String s = e.getAttribute(name);
        return s.isEmpty() ? defaultVal : s;
    }

    public static <BG, LAYER> BG parseBackground(XML document,
                                                 ImageParser imageParser,
                                                 XMLLayerParser<LAYER> layerParser,
                                                 XMLBackgroundParser<BG, LAYER> backgroundParser) {

        XML backgroundXML = document.nodes("level/background").get(0);
        Element backgroundElement = (Element) backgroundXML.node();

        int yOffset = Integer.parseInt(getOrDefault(backgroundElement, "yOffset", "0"));
        int scale = Integer.parseInt(getOrDefault(backgroundElement, "scale", "1"));

        List<XML> layersXML = backgroundXML.nodes("layer");
        List<LAYER> layers = new ArrayList<>(layersXML.size());

        for (XML layerXML : layersXML) {
            Element layerElement = (Element) layerXML.node();

            double xCoef = Double.parseDouble(getOrDefault(layerElement, "xCoef", "0"));
            double yCoef = Double.parseDouble(getOrDefault(layerElement, "yCoef", "0"));

            String src = layerElement.getAttribute("source");
            BufferedImage image = imageParser.parse(src, scale);

            layers.add(layerParser.parse(xCoef, yCoef, src, image));
        }

        return backgroundParser.parse(layers, yOffset, scale);
    }

    public static void parseEntitiesXML(XML document, XMLEntityParser callback) {

        for (XML entityXML : document.nodes("entities/entity")) {

            Element entity = (Element) entityXML.node();

            String id = entity.getAttribute("id");
            int x = Integer.parseInt(entity.getAttribute("x"));
            int y = Integer.parseInt(entity.getAttribute("y"));

            Map<String, String> tags = new LinkedHashMap<>();

            for (XML tagXML : entityXML.nodes("tags/*")) {

                Element tag = (Element) tagXML.node();
                tags.put(tag.getTagName(), tag.getTextContent());
            }

            callback.parse(id, x, y, tags);
        }
    }

    public static <T> T[] parseBlocksDictionaryXML(XML document, XMLBlockStateParser<T> callback, ArrayCreator<T> creator) {

        List<XML> statesXML = document.nodes("dictionary/blocks/state");
        T[] dictionary = creator.create(statesXML.size());

        for (int i = 0; i < dictionary.length; i++) {

            Element state = (Element) statesXML.get(i).node();
            dictionary[i] = callback.parse(state.getAttribute("id"), state.getAttribute("meta"));
        }

        return dictionary;
    }

    public static void parseScriptsXML(XML document, XMLScriptParser callback) {

        for (XML script : document.nodes("scripts/scriptObject")) {
            Element boundingBox = (Element) script.nodes("boundingBox").get(0).node();

            double x, y, w, h;
            x = Double.parseDouble(boundingBox.getAttribute("x"));
            y = Double.parseDouble(boundingBox.getAttribute("y"));
            w = Double.parseDouble(boundingBox.getAttribute("w"));
            h = Double.parseDouble(boundingBox.getAttribute("h"));

            AxisAlignedBoundingBox aabb = new StaticBoundingBox(x, y, x + w, y + h);

            Element affected = (Element) script.nodes("affected").get(0).node();

            String content = script.xpath("content/text()").get(0);

            callback.parse(aabb, affected.getAttribute("class"), content);
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
