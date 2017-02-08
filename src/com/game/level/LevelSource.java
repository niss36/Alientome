package com.game.level;

import com.game.camera.DefaultCamera;
import com.game.buffs.Buff;
import com.game.buffs.BuffBuilder;
import com.game.control.Controller;
import com.game.entities.Entity;
import com.game.entities.EntityBuilder;
import com.game.entities.EntityPlayer;
import com.game.partitioning.Tree;
import com.util.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

abstract class LevelSource {

    private static final Logger log = Logger.get();
    private final List<EntityBuilder> entitiesSpawnList = new ArrayList<>();
    private final List<BuffBuilder> buffsSpawnList = new ArrayList<>();
    private String cameraSelector;
    private String controllerSelector;
    private EntityBuilder playerBuilder;
    private Tree tree;

    void load(Level level) {

        Element root;
        try {
            root = getDataRoot(level);
        } catch (Exception e) {
            log.e("Error loading level :");
            throw new RuntimeException(e);
        }

        int spawnX = 0, spawnY = 0;

        NodeList rootNodes = root.getChildNodes();

        for (int i = 0; i < rootNodes.getLength(); i++) {
            if (rootNodes.item(i).getNodeType() == Node.ELEMENT_NODE) {
                Element node = (Element) rootNodes.item(i);

                if (node.getNodeName().equals("player")) {
                    spawnX = Integer.parseInt(node.getAttribute("spawnX"));
                    spawnY = Integer.parseInt(node.getAttribute("spawnY"));
                } else if (node.getNodeName().equals("entities")) {

                    NodeList entities = node.getElementsByTagName("entity");

                    for (int j = 0; j < entities.getLength(); j++) {

                        if (entities.item(j).getNodeType() == Node.ELEMENT_NODE) {
                            Element entity = (Element) entities.item(j);

                            entitiesSpawnList.add(EntityBuilder.parseXML(entity, level));
                        }
                    }
                } else if (node.getNodeName().equals("buffs")) {

                    NodeList buffs = node.getElementsByTagName("buff");

                    for (int j = 0; j < buffs.getLength(); j++) {

                        if (buffs.item(j).getNodeType() == Node.ELEMENT_NODE) {
                            Element buff = (Element) buffs.item(j);

                            buffsSpawnList.add(BuffBuilder.parseXML(buff));
                        }
                    }
                } else if (node.getNodeName().equals("camera")) {
                    cameraSelector = node.getAttribute("target");
                } else if (node.getNodeName().equals("controller")) {
                    controllerSelector = node.getAttribute("target");
                }
            }
        }

        playerBuilder = new EntityBuilder(Entity.PLAYER, spawnX, spawnY, level);
    }

    void reset(List<Entity> entities, List<Buff> buffs) {

        for (EntityBuilder builder : entitiesSpawnList) {
            Entity entity = builder.create();
            entities.add(entity);
            tree.add(entity);
        }

        for (BuffBuilder builder : buffsSpawnList) {
            Buff buff = builder.create();
            buffs.add(buff);
            tree.add(buff);
        }
    }

    EntityPlayer newPlayer() {
        return (EntityPlayer) playerBuilder.create();
    }

    DefaultCamera newCamera(Level level) {

        return level.parseSelectorFirst(cameraSelector).newCamera();
    }

    Controller newController(Level level) {

        return level.parseSelectorFirst(controllerSelector).newController();
    }

    Tree getTree() {
        if (tree == null)
            tree = new Tree(getMap().getWidth(), getMap().getHeight());
        return tree;
    }

    abstract Element getDataRoot(Level level) throws Exception;

    abstract BufferedImage getBackground();

    abstract LevelMap getMap();

    abstract void save();
}
