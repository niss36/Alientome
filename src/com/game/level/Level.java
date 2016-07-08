package com.game.level;

import com.game.Game;
import com.game.GameObject;
import com.game.blocks.Block;
import com.game.buffs.Buff;
import com.game.buffs.BuffBuilder;
import com.game.entities.Entity;
import com.game.entities.EntityBuilder;
import com.game.entities.EntityPlayer;
import com.game.partitioning.Tree;
import com.gui.PanelGame;
import com.util.Config;
import com.util.Direction;
import com.util.Vec2;
import com.util.visual.SpritesLoader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import static com.util.Util.log;

/**
 * This single-instance class holds the list of this world's
 * <code>Entity</code>s, <code>Buff</code>s and the <code>EntityPlayer</code>,
 * along with a <code>Tree</code> partitioning the world.
 */
public final class Level {

    private static final Level instance = new Level();
    private final ArrayList<EntityBuilder> spawnList = new ArrayList<>();
    private final ArrayList<Entity> entities = new ArrayList<>();
    private final ArrayList<BuffBuilder> buffSpawnList = new ArrayList<>();
    private final ArrayList<Buff> buffs = new ArrayList<>();
    public EntityPlayer player;
    private int levelID;
    private Tree tree;
    private int spawnX;
    private int spawnY;
    private BufferedImage background;

    private Level() {
    }

    public static Level getInstance() {
        return instance;
    }

    /**
     * Initializes the <code>Block</code> array from an image, and spawn points
     * of <code>EntityPlayer</code> and other <code>Entity</code>s from a text file.
     */
    public void init(int saveIndex) {

        while (!SpritesLoader.hasLoaded())
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        spawnList.clear();
        buffSpawnList.clear();

        levelID = LevelSaveManager.getInstance().init(saveIndex);

        log("Loading level " + levelID, 0);

        try {
            parseLevelXML(levelID);
        } catch (Exception e) {
            e.printStackTrace();
            log("Error loading level : " + e.getMessage(), 3);
        }

        background = SpritesLoader.getSprite("Level/" + levelID + "/background");

        LevelMap map = LevelMap.getInstance();

        map.init(SpritesLoader.getSprite("Level/" + levelID + "/tilemap"));

        tree = new Tree(map.getWidth(), map.getHeight());

        log("Loaded level", 0);
    }

    private void parseLevelXML(int levelID) throws Exception {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        Document document = builder.parse(ClassLoader.getSystemResourceAsStream("Level/" + levelID + "/data.xml"));

        Element root = document.getDocumentElement();
        NodeList rootNodes = root.getChildNodes();

        for (int i = 0; i < rootNodes.getLength(); i++) {
            if (rootNodes.item(i).getNodeType() == Node.ELEMENT_NODE) {
                Element node = (Element) rootNodes.item(i);

                if (node.getNodeName().equals("block")) {
                    Block.init(Integer.parseInt(node.getAttribute("width")));
                } else if (node.getNodeName().equals("player")) {
                    spawnX = Integer.parseInt(node.getAttribute("spawnX"));
                    spawnY = Integer.parseInt(node.getAttribute("spawnY"));
                } else if (node.getNodeName().equals("entities")) {

                    NodeList entities = node.getElementsByTagName("entity");

                    for (int j = 0; j < entities.getLength(); j++) {

                        if (entities.item(j).getNodeType() == Node.ELEMENT_NODE) {
                            Element entity = (Element) entities.item(j);

                            spawnList.add(EntityBuilder.parse(entity, this));
                        }
                    }
                } else if (node.getNodeName().equals("buffs")) {

                    NodeList buffs = node.getElementsByTagName("buff");

                    for (int j = 0; j < buffs.getLength(); j++) {

                        if (buffs.item(j).getNodeType() == Node.ELEMENT_NODE) {
                            Element buff = (Element) buffs.item(j);

                            buffSpawnList.add(BuffBuilder.parse(buff, this));
                        }
                    }
                }
            }
        }
    }

    public void save() {
        log("Saving level " + levelID, 0);
        LevelSaveManager.getInstance().save(levelID);
        log("Saved level", 0);
    }

    public BufferedImage getBackground() {
        return background;
    }

    /**
     * Resets the level : clears the <code>Entity</code> list, respawn the
     * <code>EntityPlayer</code> and the <code>Entity</code>s according to spawn list.
     */
    public void reset() {

        tree.clear();
        entities.clear();
        buffs.clear();

        spawnPlayer();

        for (EntityBuilder builder : spawnList) spawnEntity(builder.create());

        for (BuffBuilder builder : buffSpawnList) spawnBuff(builder.create());

        tree.add(player);
    }

    /**
     * Spawns the <code>EntityPlayer</code> at the spawn coordinates.
     */
    private void spawnPlayer() {

        player = (EntityPlayer) Entity.createFromBlockPos(Entity.PLAYER, spawnX, spawnY, this);
    }

    /**
     * Adds the given <code>Entity</code> to the <code>Entity</code> list.
     *
     * @param entity the <code>Entity</code> to add
     */
    public void spawnEntity(Entity entity) {
        entities.add(entity);
        tree.add(entity);
    }

    /**
     * Removes the given <code>Entity</code> from the <code>Entity</code> list.
     *
     * @param entity the <code>Entity</code> to remove
     */
    public void removeEntity(Entity entity) {
        entities.remove(entity);
        tree.remove(entity);
    }

    public void spawnBuff(Buff buff) {
        buffs.add(buff);
        tree.add(buff);
    }

    public void removeBuff(Buff buff) {
        buffs.remove(buff);
        tree.remove(buff);
    }

    public void move(Vec2 prevPos, GameObject object) {

        tree.move(prevPos, object);
    }

    public int updateTree() {

        return tree.updateCells();
    }

    public int countObjectsInRange(Class<? extends GameObject> type, double x, double y, double range, GameObject... excluded) {

        return tree.countObjectsInRange(type, x, y, range, excluded);
    }

    public ArrayList<GameObject> getObjectsInRange(Class<? extends GameObject> type, double x, double y, double range, GameObject... excluded) {

        return tree.getObjectsInRange(type, x, y, range, excluded);
    }

    /**
     * Called on each game update.
     *
     * @param game  the <code>Game</code> object to update
     * @param panel the <code>PanelGame</code> to update
     */
    public void update(Game game, PanelGame panel) {

        for (int i = 0; i < game.pressedKeys.size(); i++) {

            int key = game.pressedKeys.get(i);

            if (key == Config.getInstance().getInt("Key.Jump")) player.jump();
            else {
                Direction d = Direction.toDirection(key);

                if (d != null) player.move(d);
            }
        }

        panel.update(player, entities, buffs);
    }
}