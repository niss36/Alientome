package com.alientome.game.buffs.parse;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class XMLBuffParser implements BuffParser {

    private final NodeList buffNodes;
    private int index = 0;

    public XMLBuffParser(NodeList buffNodes) {

        this.buffNodes = buffNodes;
    }

    @Override
    public BuffState parse() {

        Element buffNode = (Element) buffNodes.item(index++);

        String entityID = buffNode.getAttribute("id");
        int spawnX = Integer.parseInt(buffNode.getAttribute("spawnX"));
        int spawnY = Integer.parseInt(buffNode.getAttribute("spawnY"));

        NodeList argNodes = buffNode.getElementsByTagName("arg");
        Object[] args = new Object[argNodes.getLength()];

        for (int i = 0; i < argNodes.getLength(); i++) {

            Element argNode = (Element) argNodes.item(i);
            args[i] = parseArg(argNode);
        }

        return new BuffState(entityID, spawnX, spawnY, args);
    }

    private Object parseArg(Element node) {

        return Integer.parseInt(node.getAttribute("value"));
    }
}
