package com.game;

import com.game.entities.Entity;
import com.game.entities.EntityBuilder;
import com.game.entities.EntityPlayer;
import com.gui.Panel;
import com.util.Config;
import com.util.Direction;
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

import static com.util.Util.log;

/**
 * This single-instance class holds the <code>Block</code> array defining the world,
 * the list of this world's <code>Entity</code>s and the <code>EntityPlayer</code>.
 */
public class Level {

    private static final Level instance = new Level();
    private final ArrayList<EntityBuilder> spawnList = new ArrayList<>();
    private final ArrayList<Entity> entities = new ArrayList<>();
    public EntityPlayer player;
    private byte[][] blocks;
    private int x;
    private int y;
    private int spawnX;
    private int spawnY;
    private int blockWidth;
    private BufferedImage background;

    private Level() {
    }

    public static Level getInstance() {
        return instance;
    }

    /**
     * Initializes the <code>Block</code> array from an image, and spawn points
     * of <code>EntityPlayer</code> and other <code>Entity</code>s from a text file.
     *
     * @param levelID the id of the <code>Level</code> to load
     */
    public void init(int levelID) {

        log("Loading level " + levelID, 0);

        BufferedImage image = SpritesLoader.getSprite("Level/" + levelID);

        background = SpritesLoader.getSprite("Background/" + levelID);

        BlockConstants.init();

        x = image.getWidth();
        y = image.getHeight();

        blocks = new byte[x][y];

        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                blocks[i][j] = Block.parse(image.getRGB(i, j));
            }
        }

        try {
            parseLevelXML(levelID);
        } catch (Exception e) {
            e.printStackTrace();
            log("Error loading level : " + e.getMessage(), 3);
        }

        log("Loaded level", 0);
    }

    private void parseLevelXML(int levelID) throws Exception {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        Document document = builder.parse(ClassLoader.getSystemResourceAsStream("Level/" + levelID + ".xml"));

        Element root = document.getDocumentElement();
        NodeList rootNodes = root.getChildNodes();

        for (int i = 0; i < rootNodes.getLength(); i++) {
            if (rootNodes.item(i).getNodeType() == Node.ELEMENT_NODE) {
                Element node = (Element) rootNodes.item(i);

                if (node.getNodeName().equals("block")) {
                    blockWidth = Integer.parseInt(node.getAttribute("width"));
                    Block.init(blockWidth);
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
                }
            }
        }
    }

    /**
     * @param x the x coordinate
     * @param y the y coordinate
     * @return the index of the <code>Block</code> at the specified coordinates, or
     * <code>Block.VOID</code> if out of bounds.
     */
    public byte get(int x, int y) {
        return x >= 0 && x < this.x && y >= 0 && y < this.y ? blocks[x][y] : Block.VOID;
    }

    public BufferedImage getBackground() {
        return background;
    }

    /**
     * Resets the level : clears the <code>Entity</code> list, respawn the
     * <code>EntityPlayer</code> and the <code>Entity</code>s according to spawn list.
     */
    public void reset() {

        entities.clear();

        spawnPlayer();

        for (EntityBuilder builder : spawnList) {
            spawnEntity(builder.create());
        }
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
    }

    /**
     * Removes the given <code>Entity</code> from the <code>Entity</code> list.
     *
     * @param entity the <code>Entity</code> to remove
     */
    public void removeEntity(Entity entity) {

        entities.remove(entity);
    }

    /**
     * Checks whether the given <code>Entity</code> will collide with any
     * <code>Entity</code> of the list, and calls <code>Entity.onCollidedWithEntity</code>
     * if a collision is detected.
     *
     * @param entity the <code>Entity</code> to check for collisions from
     */
    public void processEntityCollisions(Entity entity) {

        @SuppressWarnings("unchecked")
        ArrayList<Entity> aL = (ArrayList<Entity>) entities.clone();
        aL.remove(entity);

        aL.stream().filter(entity1 -> entity1.getNextBoundingBox().intersectsWith(entity.getNextBoundingBox())).forEach(entity1 -> {

            //entity.onCollidedWithEntity(entity1, entity1.getNextBoundingBox().intersectionSideWith(entity.getNextBoundingBox()));
            entity1.onCollidedWithEntity(entity, entity.getNextBoundingBox().intersectionSideWith(entity1.getNextBoundingBox()));
        });
    }

    /**
     * @param entity an <code>Entity</code>
     * @param other  an other <code>Entity</code>
     * @return whether the line of sight from <code>entity</code>
     * to <code>other</code> is obstructed by an opaque <code>Block</code>
     */
    public boolean canSeeEntity(Entity entity, Entity other) {

        Point point = new Point((int) (entity.getX() / blockWidth),
                (int) (entity.getY() / blockWidth));
        Point point1 = new Point((int) (other.getX() / blockWidth),
                (int) (other.getY() / blockWidth));

        Block[] tiles = line(point, point1);

        for (Block tile : tiles) if (tile.isOpaque()) return false;

        return true;
    }

    //Linear interpolation
    private double lERP(double start, double end, double t) {
        return start + t * (end - start);
    }

    //Linear interpolation for Points
    private Point lERPPoint(Point point, Point point1, double t) {
        return new Point((int) lERP(point.x, point1.x, t), (int) lERP(point.y, point1.y, t));
    }

    /**
     * @param point  a <code>Point</code>
     * @param point1 an other <code>Point</code>
     * @return the diagonal distance between <code>point</code> and <code>point1</code>
     */
    private int diagonalDistance(Point point, Point point1) {
        int dx = point1.x - point.x, dy = point1.y - point.x;
        return Math.max(Math.abs(dx), Math.abs(dy));
    }

    /**
     * Get the <code>Block</code>s forming the line from <code>point</code> to <code>point1</code>
     *
     * @param point  a <code>Point</code>
     * @param point1 an other <code>Point</code>
     * @return an array of <code>Block</code>s corresponding to
     * the line between <code>point</code> and <code>point1</code>
     */
    private Block[] line(Point point, Point point1) {
        int n = diagonalDistance(point, point1);

        Block[] tiles = new Block[n];

        for (int i = 0; i < n; i++) {
            double t = (double) i / n;
            tiles[i] = new Block(lERPPoint(point, point1, t));
        }
        return tiles;
    }

    /**
     * Called on each game update.
     *
     * @param game  the <code>Game</code> object to update
     * @param panel the <code>Panel</code> to update
     */
    public void update(Game game, Panel panel) {

        for (int i = 0; i < game.pressedKeys.size(); i++) {

            int key = game.pressedKeys.get(i);

            if (key == Config.getInstance().getKey("Key.Jump")) player.jump();
            else {
                Direction d = Direction.toDirection(key);

                if (d != null) player.move(d);
            }
        }

        panel.update(player, entities);
    }
}