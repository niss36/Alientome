package com.alientome.impl.level;

import com.alientome.core.collisions.AxisAlignedBoundingBox;
import com.alientome.core.util.Logger;
import com.alientome.game.DebugInfo;
import com.alientome.game.GameContext;
import com.alientome.game.GameObject;
import com.alientome.game.SpritesLoader;
import com.alientome.game.blocks.Block;
import com.alientome.game.buffs.Buff;
import com.alientome.game.commands.Command;
import com.alientome.game.commands.CommandHandler;
import com.alientome.game.commands.CommandSender;
import com.alientome.game.commands.exceptions.CommandException;
import com.alientome.game.commands.exceptions.InvalidSelectorException;
import com.alientome.game.commands.exceptions.SelectorNotFoundException;
import com.alientome.game.commands.messages.ExceptionMessage;
import com.alientome.game.control.Controller;
import com.alientome.game.entities.Entity;
import com.alientome.game.entities.EntityPlayer;
import com.alientome.game.events.GameDeathEvent;
import com.alientome.game.level.Level;
import com.alientome.game.level.LevelManager;
import com.alientome.game.level.LevelMap;
import com.alientome.game.particles.Particle;
import com.alientome.game.scripts.ScriptObject;
import com.alientome.game.background.ParallaxBackground;
import com.alientome.game.util.Selector;
import com.alientome.impl.level.source.LevelSource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

import static com.alientome.game.profiling.ExecutionTimeProfiler.theProfiler;
import static java.lang.Math.floor;

public abstract class AbstractLevel implements Level {

    protected static final Logger log = Logger.get();
    protected final Random random = new Random();
    protected final List<Entity> entities = new ArrayList<>();
    protected final List<Buff> buffs = new ArrayList<>();
    protected final List<Particle> particles = new ArrayList<>();
    protected final List<ScriptObject> scripts = new ArrayList<>();
    protected final List<QueuedEntityModification> entityModifications = new ArrayList<>();
    protected final List<Particle> removedParticles = new ArrayList<>();
    protected final DebugInfo debugInfo;
    protected final CommandHandler commandHandler;
    protected final LevelManager manager;
    protected final LevelSource source;
    protected final LevelMap map;
    protected final ParallaxBackground background;
    protected EntityPlayer player;
    protected Controller playerController;
    protected long timeTicks = 0;

    protected AbstractLevel(LevelManager manager, LevelSource source) throws IOException {

        this.manager = manager;
        this.source = source;

        debugInfo = new DebugInfo(getContext(), this);
        commandHandler = new CommandHandler(getContext());

        try {
            SpritesLoader.waitUntilLoaded();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        log.i("Loading level...");

        source.load();

        map = source.getMap();
        background = source.getBackground();
        scripts.addAll(source.getScripts());
    }

    @Override
    public void reset() {

        entities.clear();
        buffs.clear();

        player = source.newPlayer(this);
        source.reset(this, entities, buffs);
        entities.add(player);

        playerController = source.newController(this);
        playerController.addControlledDeathListener(() -> getContext().getDispatcher().submit(new GameDeathEvent()));
        timeTicks = 0;

        scripts.forEach(ScriptObject::reset);
    }

    @Override
    public void update() {

        theProfiler.startSection("Level Update");

        theProfiler.startSection("Level Update/Commands");

        commandHandler.executeAll();

        theProfiler.endSection("Level Update/Commands");

        scripts.forEach(ScriptObject::update);

        theProfiler.startSection("Level Update/Entities Update");

        entities.forEach(Entity::preUpdate);

        entities.forEach(Entity::checkCollisions);

        entities.forEach(Entity::postUpdate);

        theProfiler.endSection("Level Update/Entities Update");

        entityModifications.forEach(qEM -> qEM.doModification(entities));
        entityModifications.clear();

        particles.forEach(Particle::update);

        particles.removeAll(removedParticles);
        removedParticles.clear();

        debugInfo.registerUpdate();

        timeTicks++;

        theProfiler.endSection("Level Update");
    }

    @Override
    public void onPaused() {
        playerController.reset();
    }

    @Override
    public void spawnEntity(Entity entity) {

        entityModifications.add(new QueuedEntityModification(entity, QueuedEntityModification.Type.ADD));
    }

    @Override
    public void removeEntity(Entity entity) {

        entityModifications.add(new QueuedEntityModification(entity, QueuedEntityModification.Type.REMOVE));
    }

    @Override
    public void spawnParticle(Particle particle) {
        particles.add(particle);
    }

    @Override
    public void removeParticle(Particle particle) {
        removedParticles.add(particle);
    }

    @Override
    public List<GameObject> getObjectsInRange(Class<? extends GameObject> type, double x, double y, double range, GameObject... excluded) {

        List<GameObject> excludedList = Arrays.asList(excluded);
        List<GameObject> objects = new ArrayList<>();

        double rangeSq = range * range;

        for (Entity entity : entities)
            if (type.isInstance(entity)
                    && entity.getPos().distanceSq(x, y) < rangeSq
                    && !excludedList.contains(entity))
                objects.add(entity);

        return objects;
    }

    @Override
    public int countObjectsInRange(Class<? extends GameObject> type, double x, double y, double range, GameObject... excluded) {
        return getObjectsInRange(type, x, y, range, excluded).size();
    }

    @Override
    public void forEachCollidableBlock(AxisAlignedBoundingBox area, Consumer<Block> action) {

        int minBlockX = (int) floor(area.getMinX() / Block.WIDTH);
        int minBlockY = (int) floor(area.getMinY() / Block.WIDTH);
        int maxBlockX = (int) floor(area.getMaxX() / Block.WIDTH);
        int maxBlockY = (int) floor(area.getMaxY() / Block.WIDTH);

        for (int i = minBlockX; i <= maxBlockX; i++)
            for (int j = minBlockY; j <= maxBlockY; j++) {
                Block block = map.getBlock(i, j);
                if (block.canBeCollidedWith() && block.getBoundingBox().intersects(area))
                    action.accept(block);
            }
    }

    @Override
    public void forEachCollidableEntity(AxisAlignedBoundingBox area, Consumer<Entity> action, Entity excluded) {

        entities.stream()
                .filter(entity -> entity != excluded
                        && entity.canBeCollidedWith()
                        && entity.getNextBoundingBox().intersects(area)).forEach(action);
    }

    @Override
    public void applyScripts(Entity entity) {

        for (ScriptObject script : scripts)
            script.runOn(entity);
    }

    @Override
    public void setScriptEnabled(String id, boolean enabled) {

        for (ScriptObject script : scripts)
            if (script.is(id)) {
                script.setEnabled(enabled);
                return;
            }
    }

    @Override
    public List<Entity> selectAll(Selector selector) throws CommandException {

        List<Entity> found = new ArrayList<>();

        switch (selector.type) {

            case PLAYER:
                found.add(player);
                break;

            case CONTROLLED:
                found.add(getControlled());
                break;

            case ALL:
                Entity e = tryGetByIndex(selector);
                if (e == null)
                    found.addAll(entities);
                else
                    found.add(e);
                break;

            default:
                throw new InvalidSelectorException(selector);
        }

        if (found.size() == 0)
            throw new SelectorNotFoundException(selector);
        return found;
    }

    @Override
    public Entity selectFirst(Selector selector) throws CommandException {

        switch (selector.type) {

            case PLAYER:
                return player;

            case CONTROLLED:
                return getControlled();

            case ALL:
                Entity e = tryGetByIndex(selector);
                return e == null ? entities.get(0) : e;

            default:
                throw new InvalidSelectorException(selector);
        }
    }

    private Entity tryGetByIndex(Selector selector) throws CommandException {

        String indexStr = selector.args.get("index");

        if (indexStr != null) {
            int index = Command.parseInt(indexStr);
            if (index > entities.size())
                throw new SelectorNotFoundException(selector);
            return entities.get(index);
        }

        return null;
    }

    @Override
    public void executeCommand(CommandSender sender, String command) {

        if (command.startsWith("/"))
            command = command.substring(1);

        String[] commandArgs = command.split(" ");
        String commandID = commandArgs[0];
        commandArgs = Arrays.copyOfRange(commandArgs, 1, commandArgs.length);

        try {
            commandHandler.queueCommand(commandID, sender, commandArgs);
        } catch (CommandException e) {
            sender.addConsoleMessage(new ExceptionMessage(e));
        }
    }

    @Override
    public GameContext getContext() {
        return manager.getContext();
    }

    @Override
    public Random getRandom() {
        return random;
    }

    @Override
    public DebugInfo getDebugInfo() {
        return debugInfo;
    }

    @Override
    public LevelMap getMap() {
        return map;
    }

    @Override
    public long getTimeTicks() {
        return timeTicks;
    }

    @Override
    public EntityPlayer getPlayer() {
        return player;
    }

    @Override
    public Entity getControlled() {
        return playerController.getControlled();
    }

    @Override
    public void setControlled(Entity target) {
        getControlled().onControlLost();
        playerController = target.newController();
    }

    @Override
    public void setController(Controller controller) {
        getControlled().setController(controller);
        playerController = controller;
    }

    @Override
    public Controller getController() {
        return playerController;
    }
}
