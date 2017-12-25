package com.alientome.impl.level.source;

import com.alientome.core.SharedInstances;
import com.alientome.core.SharedNames;
import com.alientome.core.util.FileUtils;
import com.alientome.core.util.Logger;
import com.alientome.core.util.Util;
import com.alientome.game.blocks.Block;
import com.alientome.game.blocks.parse.BlockState;
import com.alientome.game.buffs.Buff;
import com.alientome.game.camera.Camera;
import com.alientome.game.control.Controller;
import com.alientome.game.entities.Entity;
import com.alientome.game.entities.EntityPlayer;
import com.alientome.game.entities.parse.EntityState;
import com.alientome.game.level.Level;
import com.alientome.game.level.LevelMap;
import com.alientome.game.parse.LvlParser;
import com.alientome.game.registry.GameRegistry;
import com.alientome.game.registry.Registry;
import com.alientome.game.scripts.ScriptObject;
import com.alientome.game.util.EntityTags;
import com.alientome.game.util.Layer;
import com.alientome.game.util.ParallaxBackground;
import com.alientome.impl.level.source.uri.URIProvider;
import com.alientome.script.Script;
import com.alientome.script.ScriptException;
import com.alientome.script.ScriptParser;
import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import org.w3c.dom.Element;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;

public abstract class CompoundLevelSource implements LevelSource {

    protected static final Logger log = Logger.get();
    private final ScriptParser parser;
    private final List<EntityState> entitiesSpawnList = new ArrayList<>();
//    protected final List<BuffState> buffsSpawnList = new ArrayList<>();
    private final List<ScriptObject> scripts = new ArrayList<>();
    private ParallaxBackground background;
    private LevelMap map;
    private EntityState playerState;

    public CompoundLevelSource(ScriptParser parser) {
        this.parser = parser;
    }

    @Override
    public void load() throws IOException {

        try (URIProvider provider = newProvider()) {

            XML levelXML = new XMLDocument(provider.get("level.xml"));

            Element dimension = (Element) levelXML.nodes("level/dimension").get(0).node();
            int width = Integer.parseInt(dimension.getAttribute("width"));
            int height = Integer.parseInt(dimension.getAttribute("height"));

            background = LvlParser.parseBackground(levelXML,
                    (path, scale) -> {
                        try {
                            return Util.scale(ImageIO.read(provider.get(path).toURL()), scale);
                        } catch (IOException e) {
                            throw new UncheckedIOException(e);
                        }
                    },
                    (xCoef, yCoef, src, image) -> new Layer(image, xCoef, yCoef),
                    (layers, yOffset, scale) -> new ParallaxBackground(layers, yOffset));

            XML dictionaryXML = new XMLDocument(provider.get("dictionary.xml"));

            BlockState[] blocksDictionary = LvlParser.parseBlocksDictionaryXML(dictionaryXML,
                    (id, meta) -> new BlockState(id, Byte.parseByte(meta)),
                    BlockState[]::new);

            Block[][] blocks = new Block[width][height];

            LvlParser.parseMap(FileUtils.openStream(provider.get("map")), width, height,
                    (x, y, stateIndex) -> blocks[x][y] = Block.create(x, y, blocksDictionary[stateIndex]));

            map = new LevelMap(blocks);

            XML entitiesXML = new XMLDocument(provider.get("entities.xml"));

            LvlParser.parseEntitiesXML(entitiesXML, (id, x, y, tags) -> {

                EntityState entity = new EntityState(id, x, y, new EntityTags(tags));
                if (id.equals("player"))
                    playerState = entity;
                else
                    entitiesSpawnList.add(entity);
            });

            XML scriptsXML = new XMLDocument(provider.get("scripts.xml"));

            GameRegistry registry = SharedInstances.get(SharedNames.REGISTRY);
            Registry<Class<? extends Entity>> entityRegistry = registry.getEntitiesRegistry();

            LvlParser.parseScriptsXML(scriptsXML, (aabb, affected, content) -> {
                Class<? extends Entity> affectedClass = affected.equals("*") ? Entity.class : entityRegistry.get(affected);
                if (affectedClass == null)
                    throw new RuntimeException("Unknown entity type : " + affected);
                Script script;
                try {
                    script = parser.parseScript(content);
                } catch (ScriptException e) {
                    throw new RuntimeException(e);
                }
                scripts.add(new ScriptObject(aabb, affectedClass, script));
            });
        }
    }

    @Override
    public void reset(Level level, List<Entity> entities, List<Buff> buffs) {

        for (EntityState state : entitiesSpawnList)
            entities.add(Entity.create(state, level));

        /*for (BuffState state : buffsSpawnList)
            buffs.add(Buff.create(state));*/
    }

    @Override
    public EntityPlayer newPlayer(Level level) {
        return (EntityPlayer) Entity.create(playerState, level);
    }

    @Override
    public Camera newCamera(Level level) {
        return level.getPlayer().newCamera();
    }

    @Override
    public Controller newController(Level level) {
        return level.getPlayer().newController();
    }

    @Override
    public LevelMap getMap() {
        return map;
    }

    @Override
    public ParallaxBackground getBackground() {
        return background;
    }

    @Override
    public List<ScriptObject> getScripts() {
        return scripts;
    }

    protected abstract URIProvider newProvider() throws IOException;
}
