package com.alientome.editors.level;

import com.alientome.core.collisions.AxisAlignedBoundingBox;
import com.alientome.core.util.FileUtils;
import com.alientome.core.util.WrappedXML;
import com.alientome.editors.level.background.Background;
import com.alientome.editors.level.background.Layer;
import com.alientome.editors.level.registry.EditorRegistry;
import com.alientome.editors.level.state.*;
import com.alientome.editors.level.util.Colors;
import com.alientome.game.parse.LvlParser;
import org.w3c.dom.Document;
import org.xembly.Directives;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.List;

import static com.alientome.core.util.Util.parseXMLNew;
import static com.alientome.editors.level.state.BlockState.WIDTH;
import static com.alientome.editors.level.util.Util.*;

public class Level {

    private final Path tempDirectory;
    private final BlockState defaultState;
    private final List<ScriptObject> scripts;
    private final List<Layer> layers;
    private File source;
    private Background background;
    private BlockState[][] tiles;
    private Entity[][] entities;
    private List<Entity> entityList = new ArrayList<>();
    private BufferedImage minimap;
    private int playerX = -1;
    private int playerY = -1;

    public Level(EditorRegistry registry, List<ScriptObject> scripts, List<Layer> layers, int width, int height) {

        try {
            tempDirectory = FileUtils.createTempDirectory("Unnamed", "Level Editor");
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        this.scripts = scripts;
        scripts.clear();
        this.layers = layers;
        layers.clear();

        defaultState = registry.getBlocksRegistry().get("air:0");

        tiles = new BlockState[width][height];

        fillNullTiles();

        entities = new Entity[width][height];

        minimap = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        background = new Background(layers, 1, 0);
    }

    public Level(EditorRegistry registry, List<ScriptObject> scripts, List<Layer> layers, File source) throws IOException {

        if (!source.exists())
            throw new FileNotFoundException(source.toString());

        tempDirectory = FileUtils.createTempDirectory(source.getName() + "_", "Level Editor");

        this.scripts = scripts;
        scripts.clear();
        this.layers = layers;
        layers.clear();

        defaultState = registry.getBlocksRegistry().get("air:0");

        this.source = source;

        Map<String, String> env = new HashMap<>();
        env.put("create", "false");
        env.put("encoding", "UTF-8");

        try (FileSystem fs = FileSystems.newFileSystem(FileUtils.toZip(source), env)) {
            FileUtils.copyDirectory(fs.getPath("/"), tempDirectory, StandardCopyOption.REPLACE_EXISTING);
        }

        WrappedXML level = parseXMLNew(tempDirectory.resolve("level.xml").toUri());

        WrappedXML dimension = level.getFirst("level/dimension");

        int width = dimension.getAttrInt("width");
        int height = dimension.getAttrInt("height");

        background = LvlParser.parseBackground(level,
                (path, scale) -> {
                    try {
                        return ImageIO.read(tempDirectory.resolve(path).toFile());
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                }, (xCoef, yCoef, src, image) -> new Layer(src, image, xCoef, yCoef),
                (layers1, yOffset, scale) -> {
                    layers.addAll(layers1);
                    return new Background(layers, scale, yOffset);
                });

        WrappedXML dictionary = parseXMLNew(tempDirectory.resolve("dictionary.xml").toUri());

        BlockState[] blocksDictionary = LvlParser.parseBlocksDictionaryXML(dictionary,
                (id, meta) -> registry.getBlocksRegistry().get(id + ":" + meta),
                BlockState[]::new);

        minimap = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        tiles = new BlockState[width][height];
        entities = new Entity[width][height];

        LvlParser.parseMap(FileUtils.openStream(tempDirectory.resolve("map").toUri()), width, height, (x, y, stateIndex) -> {
            BlockState state = blocksDictionary[stateIndex];
            minimap.setRGB(x, y, state.sprite.color.getRGB());
            tiles[x][y] = state;
        });

        WrappedXML entitiesXML = parseXMLNew(tempDirectory.resolve("entities.xml").toUri());

        LvlParser.parseEntitiesXML(entitiesXML, (id, x, y, tags) -> {
            EntityState state = registry.getEntitiesRegistry().get(id);

            if (id.equals("player")) {
                playerX = x;
                playerY = y;
            }

            Entity e = new Entity(state, x, y, tags);
            entities[x][y] = e;
            entityList.add(e);
            minimap.setRGB(x, y, state.sprite.color.getRGB());
        });

        WrappedXML scriptsXML = parseXMLNew(tempDirectory.resolve("scripts.xml").toUri());

        LvlParser.parseScriptsXML(scriptsXML,
                (id, enabled, aabb, affected, content) ->
                        this.scripts.add(new ScriptObject(id, enabled, aabb, affected, content)));
    }

    public void draw(Graphics g, boolean showGrid) {

        for (int i = 0; i < tiles.length; i++)
            for (int j = 0; j < tiles[0].length; j++) {
                tiles[i][j].sprite.draw(g, i * WIDTH, j * WIDTH, Colors.GRID, showGrid);
                Entity entity = entities[i][j];
                if (entity != null)
                    entity.draw(g, showGrid);
            }

        g.setColor(Colors.SCRIPT);
        for (ScriptObject script : scripts)
            script.draw(g);
    }

    public void setTile(int x, int y, BlockState state) {
        if (state == null)
            setTile(x, y, defaultState);
        else {
            tiles[x][y] = state;
            minimap.setRGB(x, y, state.sprite.color.getRGB());
        }
    }

    public void addEntity(int x, int y, EntityState state) {

        removeEntity(x, y);

        if (state.id.equals("player")) {
            if (checkBounds(playerX, playerY))
                removeEntityInternal(playerX, playerY);
            playerX = x;
            playerY = y;
        }

        Entity e = new Entity(state, x, y, new LinkedHashMap<>());
        entities[x][y] = e;
        entityList.add(e);
        minimap.setRGB(x, y, state.sprite.color.getRGB());
    }

    public void removeEntity(int x, int y) {
        Entity e = entities[x][y];
        if (e != null && e.state.id.equals("player"))
            playerX = playerY = -1;
        removeEntityInternal(x, y);
    }

    private void removeEntityInternal(int x, int y) {
        Entity e = entities[x][y];
        if (e != null)
            entityList.remove(e);
        entities[x][y] = null;
        minimap.setRGB(x, y, tiles[x][y].sprite.color.getRGB());
    }

    public Entity getEntityAt(int pxX, int pxY) {

        for (Entity e : entityList) {

            int[] eC = e.getScreenCoordinates();

            int pxXRel = pxX - eC[0];
            int pxYRel = pxY - eC[1];

            if (0 <= pxXRel && pxXRel <= e.state.sprite.dimension.width && 0 <= pxYRel && pxYRel <= e.state.sprite.dimension.height)
                return e;
        }

        return null;
    }

    public ScriptObject getScriptAt(int pxX, int pxY) {

        for (ScriptObject script : scripts)
            if (script.aabb.containsPoint(pxX, pxY))
                return script;

        return null;
    }

    public boolean checkBounds(int x, int y) {
        return checkArrayBounds(x, getWidth()) && checkArrayBounds(y, getHeight());
    }

    public int getWidth() {
        return tiles.length;
    }

    public int getHeight() {
        return tiles[0].length;
    }

    public BufferedImage getMinimap() {
        return minimap;
    }

    public Background getBackground() {
        return background;
    }

    public LevelState copyState(String name) {
        return new LevelState(name,
                deepCopy(tiles, new BlockState[tiles.length][tiles[0].length]),
                deepCopy(entityList),
                deepCopy(scripts),
                playerX, playerY);
    }

    public void setState(LevelState state) {

        tiles = state.tiles;
        entities = new Entity[getWidth()][getHeight()];
        entityList = state.entityList;
        for (Entity e : entityList)
            entities[e.x][e.y] = e;
        update(scripts, state.scripts);
        playerX = state.playerX;
        playerY = state.playerY;

        setMinimapToState();
    }

    private void setMinimapToState() {
        int width = getWidth();
        int height = getHeight();

        if (width != minimap.getWidth() || height != minimap.getHeight())
            minimap = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        for (int i = 0; i < width; i++)
            for (int j = 0; j < height; j++) {
                minimap.setRGB(i, j, tiles[i][j].sprite.color.getRGB());
                Entity entity = entities[i][j];
                if (entity != null)
                    minimap.setRGB(i, j, entity.state.sprite.color.getRGB());
            }
    }

    public Path getTempDirectory() {
        return tempDirectory;
    }

    public boolean canPlay() {
        return checkBounds(playerX, playerY);
    }

    public boolean canSave() {
        return source != null;
    }

    public File getSource() {
        return source;
    }

    public void setSource(File source) {
        this.source = source;
    }

    public void save() throws IOException {
        saveTo(source);
    }

    public void saveToTemp() throws IOException {

        File dir = tempDirectory.toFile();

        File level = new File(dir, "level.xml");
        File dictionary = new File(dir, "dictionary.xml");
        File entitiesXml = new File(dir, "entities.xml");
        File scriptsXml = new File(dir, "scripts.xml");
        File map = new File(dir, "map");

        int width = getWidth();
        int height = getHeight();

        List<BlockState> blockStates = new ArrayList<>();

        try (DataOutputStream stream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(map)))) {

            for (int i = 0; i < height; i++)
                for (int j = 0; j < width; j++) {
                    BlockState blockState = tiles[j][i];

                    assert blockState != null;

                    if (!blockStates.contains(blockState))
                        blockStates.add(blockState);

                    stream.writeByte(blockStates.indexOf(blockState));
                }
        }

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            {
                Document doc = builder.newDocument();
                doc.setXmlStandalone(true);

                Directives directives = new Directives().add("level");
                directives.add("dimension").attr("width", width).attr("height", height).up();
                directives.add("background").attr("scale", background.scale).attr("yOffset", background.yOffset);
                layers.stream().map(Level::createLayer).forEach(directives::append);

                new Xembler(directives).apply(doc);

                saveStandardised(doc, new StreamResult(level));
            }

            {
                Document doc = builder.newDocument();
                doc.setXmlStandalone(true);

                Directives directives = new Directives().add("dictionary").add("blocks");
                blockStates.stream().map(Level::createBlockState).forEach(directives::append);

                new Xembler(directives).apply(doc);

                saveStandardised(doc, new StreamResult(dictionary));
            }

            {
                Document doc = builder.newDocument();
                doc.setXmlStandalone(true);

                Directives directives = new Directives().add("entities");
                entityList.stream().map(Level::createEntity).forEach(directives::append);

                new Xembler(directives).apply(doc);

                saveStandardised(doc, new StreamResult(entitiesXml));
            }

            {
                Document doc = builder.newDocument();
                doc.setXmlStandalone(true);

                Directives directives = new Directives().add("scripts");
                scripts.stream().map(Level::createScript).forEach(directives::append);

                new Xembler(directives).apply(doc);

                saveStandardised(doc, new StreamResult(scriptsXml));
            }

        } catch (ParserConfigurationException | ImpossibleModificationException | TransformerException e) {
            throw new RuntimeException(e);
        }
    }

    private static Directives createLayer(Layer l) {

        return new Directives().add("layer").attr("source", l.name).attr("xCoef", l.xCoef).attr("yCoef", l.yCoef).up();
    }

    private static Directives createBlockState(BlockState s) {

        return new Directives().add("state").attr("id", s.id).attr("meta", s.metadata).up();
    }

    private static Directives createEntity(Entity e) {

        Directives directives = new Directives().add("entity").attr("id", e.state.id).attr("x", e.x).attr("y", e.y);
        if (e.tagsMap != null && !e.tagsMap.isEmpty())
            directives.add("tags").add(e.tagsMap).up();
        return directives.up();
    }

    private static Directives createScript(ScriptObject s) {
        return new Directives().add("scriptObject")
                .attr("id", s.id == null ? "" : s.id).attr("enabled", s.enabled).add("boundingBox")
                .attr("x", (int) s.aabb.getMinX()).attr("y", (int) s.aabb.getMinY())
                .attr("w", (int) s.aabb.getWidth()).attr("h", (int) s.aabb.getHeight()).up()
                .add("affected").attr("class", s.affected).up()
                .add("content").set(s.content).up().up();
    }

    public void saveTo(File target) throws IOException {

        if (target == null)
            throw new NullPointerException("Null target");

        saveToTemp();

        Map<String, String> env = new HashMap<>();
        env.put("create", "true");
        env.put("encoding", "UTF-8");

        try (FileSystem fs = FileSystems.newFileSystem(FileUtils.toZip(target), env)) {

            FileUtils.copyDirectory(tempDirectory, fs.getPath("/"), StandardCopyOption.REPLACE_EXISTING);
        }
    }

    public void resizeTo(int width, int height, int xOrientation, int yOrientation) {

        BlockState[][] newTiles = new BlockState[width][height];
        Entity[][] newEntities = new Entity[width][height];

        int oldWidth = getWidth();
        int oldHeight = getHeight();

        int xOffset = xOrientation == 0 ? width - oldWidth : 0;
        int yOffset = yOrientation == 0 ? height - oldHeight : 0;

        for (int i = 0; i < width; i++) {

            int srcX = i - xOffset,
                    srcPos = Math.max(0, -yOffset),
                    destPos = yOffset + srcPos,
                    length = Math.min(height, oldHeight);

            if (checkArrayBounds(srcX, oldWidth)) {
                System.arraycopy(tiles[srcX], srcPos, newTiles[i], destPos, length);
                System.arraycopy(entities[srcX], srcPos, newEntities[i], destPos, length);
            }
        }

        tiles = newTiles;
        entities = newEntities;

        if (xOffset != 0 || yOffset != 0) {
            for (int i = 0; i < entityList.size(); i++)
                offsetEntity(i, xOffset, yOffset);
            for (int i = 0; i < scripts.size(); i++)
                offsetScript(i, xOffset, yOffset);
        }

        fillNullTiles();

        setMinimapToState();
    }

    private void offsetEntity(int index, int xOffset, int yOffset) {

        Entity e = entityList.get(index);

        int x = e.x + xOffset;
        int y = e.y + yOffset;

        Entity n = new Entity(e.state, x, y, e.tagsMap);

        entityList.set(index, n);

        entities[x][y] = n;

        if (e.state.id.equals("player")) {
            playerX = x;
            playerY = y;
        }
    }

    private void offsetScript(int index, int xOffset, int yOffset) {

        ScriptObject s = scripts.get(index);

        AxisAlignedBoundingBox aabb = s.aabb.offset(xOffset * BlockState.WIDTH, yOffset * BlockState.WIDTH);

        ScriptObject n = new ScriptObject(s.id, s.enabled, aabb, s.affected, s.content);

        scripts.set(index, n);
    }

    private void fillNullTiles() {
        for (int i = 0; i < tiles.length; i++)
            for (int j = 0; j < tiles[i].length; j++) {
                BlockState state = tiles[i][j];
                if (state == null)
                    tiles[i][j] = defaultState;
            }
    }
}
