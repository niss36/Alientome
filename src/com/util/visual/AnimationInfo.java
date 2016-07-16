package com.util.visual;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class AnimationInfo {

    final String directory;
    final int spritesCount;
    final int delay;
    final int[] offsetsX;
    final int[] offsetsY;
    final boolean loop;

    private AnimationInfo(String directory, int spritesCount, int delay, int[] offsetsX, int[] offsetsY, boolean loop) {
        this.directory = directory;
        this.spritesCount = spritesCount;
        this.delay = delay;
        this.offsetsX = offsetsX;
        this.offsetsY = offsetsY;
        this.loop = loop;
    }

    static AnimationInfo parse(String path, Element animationNode) {

        int delay = Integer.parseInt(animationNode.getAttribute("delay"));
        int offsetGlobal = getAttributeOrDefault(animationNode, "offset", 0);
        int offsetXGlobal = getAttributeOrDefault(animationNode, "offsetX", offsetGlobal);
        int offsetYGlobal = getAttributeOrDefault(animationNode, "offsetY", offsetGlobal);

        String attributeValue = animationNode.getAttribute("loop");
        boolean loop = attributeValue.isEmpty() || Boolean.parseBoolean(attributeValue);
        int spritesCount;
        int[] offsetsX;
        int[] offsetsY;

        if (animationNode.hasChildNodes()) {
            NodeList sprites = animationNode.getElementsByTagName("sprite");
            spritesCount = sprites.getLength();

            offsetsX = new int[spritesCount];
            offsetsY = new int[spritesCount];

            for (int i = 0; i < spritesCount; i++) {
                Element sprite = (Element) sprites.item(i);

                offsetsX[i] = getAttributeOrDefault(sprite, "offsetX", offsetXGlobal)/* * 2*/;
                offsetsY[i] = getAttributeOrDefault(sprite, "offsetY", offsetYGlobal)/* * 2*/;
            }
        } else {
            spritesCount = Integer.parseInt(animationNode.getAttribute("spritesCount"));

            offsetsX = new int[spritesCount];
            offsetsY = new int[spritesCount];

            for (int i = 0; i < spritesCount; i++) {

                offsetsX[i] = offsetXGlobal/* * 2*/;
                offsetsY[i] = offsetYGlobal/* * 2*/;
            }
        }

        return new AnimationInfo(path, spritesCount, delay, offsetsX, offsetsY, loop);
    }

    private static int getAttributeOrDefault(Element element, String attributeName, int defaultValue) {

        String attributeValue = element.getAttribute(attributeName);
        return attributeValue.isEmpty() ? defaultValue : Integer.parseInt(attributeValue);
    }
}
