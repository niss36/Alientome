package com.game.level;

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
import com.util.Line;
import com.util.Vec2;
import com.util.visual.SpritesLoader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import static com.util.Util.center;
import static com.util.Util.log;

/**
 * This single-instance class holds the list of this world's
 * <code>Entity</code>s, <code>Buff</code>s and the <code>EntityPlayer</code>,
 * along with a <code>Tree</code> partitioning the world.
 */
public final class Level {

    private static final Level instance = new Level();
    private final List<EntityBuilder> spawnList = new ArrayList<>();
    private final List<BuffBuilder> buffSpawnList = new ArrayList<>();
    private final List<Entity> entities = new ArrayList<>();
    private final List<Buff> buffs = new ArrayList<>();
    private final List<Line> lines = new ArrayList<>();
    private final Point origin = new Point(0, 0);
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

                            buffSpawnList.add(BuffBuilder.parse(buff));
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
    }

    /**
     * Spawns the <code>EntityPlayer</code> at the spawn coordinates.
     */
    private void spawnPlayer() {

        player = (EntityPlayer) Entity.createFromBlockPos(Entity.PLAYER, spawnX, spawnY, this);
        tree.add(player);
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

    private int updateTree() {

        return tree.updateCells();
    }

    public int countObjectsInRange(Class<? extends GameObject> type, double x, double y, double range, GameObject... excluded) {

        return tree.countObjectsInRange(type, x, y, range, excluded);
    }

    public ArrayList<GameObject> getObjectsInRange(Class<? extends GameObject> type, double x, double y, double range, GameObject... excluded) {

        return tree.getObjectsInRange(type, x, y, range, excluded);
    }

    void addLine(Line line) {

        if (Config.getInstance().getBoolean("Debug.ShowSightLines")) lines.add(line);
    }

    /**
     * Called on each game update.
     *
     * @param pressedKeys a list containing the keys currently pressed in order to apply movement to the player
     * @param panel       the <code>PanelGame</code> to update
     */
    public void update(List<Integer> pressedKeys, PanelGame panel) {

        for (Integer pressedKey : pressedKeys) {

            if (pressedKey == Config.getInstance().getInt("Key.Jump")) player.jump();
            else {
                Direction d = Direction.toDirection(pressedKey);

                if (d != null) player.move(d);
            }
        }

        lines.clear();

        int newX = (int) player.getPos().x - panel.getWidth() / 2;
        int newY = (int) player.getPos().y - panel.getHeight() / 2;

        int maxX = LevelMap.getInstance().getWidth() * Block.width;

        if (newX < 0) newX = 0;
        else if (newX + panel.getWidth() > maxX) newX = maxX - panel.getWidth();

        int maxY = LevelMap.getInstance().getHeight() * Block.width;

        if (newY < 0) newY = 0;
        else if (newY + panel.getHeight() > maxY) newY = maxY - panel.getHeight();

        origin.move(newX, newY);

        player.onUpdate();

        //noinspection ForLoopReplaceableByForEach
        for (int i = 0; i < entities.size(); i++) entities.get(i).onUpdate();

        panel.update(updateTree());
    }

    public void draw(Graphics g, boolean debug) {

        if (background != null)
            g.drawImage(background,
                    center(g.getClipBounds().getWidth(), background.getWidth()),
                    center(g.getClipBounds().getHeight(), background.getHeight()),
                    null);

        for (int x = origin.x / Block.width - 1; x < (origin.x + g.getClipBounds().width) / Block.width + 1; x++)
            for (int y = origin.y / Block.width - 1; y < (origin.y + g.getClipBounds().height) / Block.width + 1; y++)
                if (LevelMap.getInstance().checkBounds(x, y))
                    LevelMap.getInstance().getBlock(x, y, false).draw(g, origin, debug);

        for (Buff buff : buffs) buff.draw(g, origin, debug);

        for (Entity entity : entities) entity.draw(g, origin, debug);

        if (player != null) player.draw(g, origin, debug);

        if (debug) for (Line line : lines) line.draw(g, origin);
    }
}