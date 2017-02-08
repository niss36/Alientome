package com.game.level;

import com.events.GameEventDispatcher;
import com.events.GameEventListener;
import com.events.GameEventType;
import com.events.ListenersCache;
import com.game.camera.Camera;
import com.game.GameObject;
import com.game.blocks.Block;
import com.game.buffs.Buff;
import com.game.command.CommandHandler;
import com.game.command.CommandSender;
import com.game.control.Controller;
import com.game.entities.Entity;
import com.game.entities.EntityPlayer;
import com.game.partitioning.Tree;
import com.keybindings.MappedKeyEvent;
import com.settings.Config;
import com.util.*;
import com.util.visual.GameGraphics;
import com.util.visual.SpritesLoader;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

import static com.util.Util.center;
import static com.util.Util.isSelector;
import static com.util.profile.ExecutionTimeProfiler.theProfiler;

/**
 * This class holds a list of this world's
 * <code>Entity</code>s, <code>Buff</code>s and the <code>EntityPlayer</code>,
 * along with a <code>Tree</code> partitioning the world.
 */
public class Level {

    private static final Logger log = Logger.get();
    public final LevelMap map;
    private final List<Entity> entities = new ArrayList<>();
    private final List<DelayedEntityListModification> delayedModifications = new ArrayList<>();
    private final List<Buff> buffs = new ArrayList<>();
    private final List<Line> lines = new ArrayList<>();
    private final Point origin = new Point(0, 0);
    private final Tree tree;
    private final BufferedImage background;
    private final LevelSource levelSource;
    private final DebugInfo debugInfo = new DebugInfo(this);
    private final CommandHandler commandHandler = new CommandHandler();
    private EntityPlayer player;
    private Camera playerCamera;
    private Controller playerController;
    private long timeTicks = 0;

    public Level(LevelSource levelSource) {

        this.levelSource = levelSource;

        try {
            SpritesLoader.waitUntilLoaded();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        log.i("Loading level...");

        levelSource.load(this);

        background = levelSource.getBackground();

        map = levelSource.getMap();

        tree = levelSource.getTree();

        GameEventListener listener = e -> playerController.reset();

        ListenersCache.register(this, listener);
        GameEventDispatcher.getInstance().register(GameEventType.GAME_RESUME, listener);

        log.i("Loaded level");
    }

    public void save() {
        log.i("Saving level...");
        levelSource.save();
        ListenersCache.unregister(this);
        log.i("Saved level");
    }

    /**
     * Resets the level : clears the <code>Entity</code> list, respawn the
     * <code>EntityPlayer</code> and the <code>Entity</code>s according to spawn list.
     */
    public void reset() {

        tree.clear();
        entities.clear();
        delayedModifications.clear();
        buffs.clear();

        player = levelSource.newPlayer();
        tree.add(player);
        levelSource.reset(entities, buffs);
        playerCamera = levelSource.newCamera(this);
        playerController = levelSource.newController(this);
        playerController.addControlledDeathListener(() -> GameEventDispatcher.getInstance().submit(null, GameEventType.GAME_DEATH));

        timeTicks = 0;
    }

    public DebugInfo getDebugInfo() {
        return debugInfo;
    }

    /**
     * Adds the given <code>Entity</code> to the <code>Entity</code> list.
     *
     * @param entity the <code>Entity</code> to add
     */
    public void spawnEntity(Entity entity) {
        DelayedEntityListModification delayedModification = new DelayedEntityListModification(entity, ModificationType.ADD);
        delayedModifications.add(delayedModification);
    }

    /**
     * Removes the given <code>Entity</code> from the <code>Entity</code> list.
     *
     * @param entity the <code>Entity</code> to remove
     */
    public void removeEntity(Entity entity) {
        DelayedEntityListModification delayedModification = new DelayedEntityListModification(entity, ModificationType.REMOVE);
        delayedModifications.add(delayedModification);
    }

    private void spawnEntityInternal(Entity entity) {
        entities.add(entity);
        tree.add(entity);
    }

    private void removeEntityInternal(Entity entity) {
        entities.remove(entity);
        tree.remove(entity);
    }

    public void move(Vec2 prevPos, GameObject object) {

        tree.move(prevPos, object);
    }

    public int countObjectsInRange(Class<? extends GameObject> type, double x, double y, double range, GameObject... excluded) {

        return tree.countObjectsInRange(type, x, y, range, excluded);
    }

    public ArrayList<GameObject> getObjectsInRange(Class<? extends GameObject> type, double x, double y, double range, GameObject... excluded) {

        return tree.getObjectsInRange(type, x, y, range, excluded);
    }

    void addLine(Line line) {

        if (Config.getInstance().getBoolean("showSightLines")) lines.add(line);
    }

    /**
     * Called on each game update.
     */
    public void update() {

        theProfiler.startSection("Level Update");

        lines.clear();

        theProfiler.startSection("Level Update/Commands");

        commandHandler.executeAll();

        theProfiler.endSection("Level Update/Commands");

        theProfiler.startSection("Level Update/Entities Update");

        player.preUpdate();
        entities.forEach(Entity::preUpdate);

        player.doBlockCollisions();
        entities.forEach(Entity::doBlockCollisions);

        theProfiler.startSection("Level Update/Tree Update");
        int updatedCells = tree.updateCells();
        theProfiler.endSection("Level Update/Tree Update");

        player.postUpdate();
        entities.forEach(Entity::postUpdate);

        theProfiler.endSection("Level Update/Entities Update");

        delayedModifications.forEach(DelayedEntityListModification::doModification);
        delayedModifications.clear();

        debugInfo.registerUpdate();
        debugInfo.registerCellUpdates(updatedCells);

        playerCamera.update();

        timeTicks++;

        theProfiler.endSection("Level Update");
    }

    public void draw(Graphics g, boolean debug, double interpolation) {

        theProfiler.startSection("Rendering/Drawing Level");

        theProfiler.startSection("Rendering/Drawing Level/Background");
        Rectangle clipBounds = g.getClipBounds();
        if (background != null)
            g.drawImage(background,
                    center(clipBounds.width, background.getWidth()),
                    center(clipBounds.height, background.getHeight()),
                    null);
        theProfiler.endSection("Rendering/Drawing Level/Background");

        updateOrigin(clipBounds, interpolation);

        GameGraphics graphics = new GameGraphics(g, origin, timeTicks, interpolation);

        theProfiler.startSection("Rendering/Drawing Level/Drawing Blocks");
        for (int x = origin.x / Block.WIDTH - 1; x < (origin.x + clipBounds.width) / Block.WIDTH + 1; x++)
            for (int y = origin.y / Block.WIDTH - 1; y < (origin.y + clipBounds.height) / Block.WIDTH + 1; y++)
                if (map.checkBounds(x, y))
                    map.getBlock(x, y, false).draw(graphics, debug);
        theProfiler.endSection("Rendering/Drawing Level/Drawing Blocks");

        theProfiler.startSection("Rendering/Drawing Level/Drawing Entities");

        for (Buff buff : buffs) buff.draw(graphics, debug);

        for (Entity entity : entities) entity.draw(graphics, debug);

        player.draw(graphics, debug);
        theProfiler.endSection("Rendering/Drawing Level/Drawing Entities");

        if (debug) for (Line line : lines) line.draw(graphics);

        theProfiler.endSection("Rendering/Drawing Level");
    }

    private void updateOrigin(Rectangle clipBounds, double interpolation) {

        theProfiler.startSection("Rendering/Drawing Level/Origin Update");

        playerCamera.transform(origin, interpolation, clipBounds, map.getBounds());

        theProfiler.endSection("Rendering/Drawing Level/Origin Update");
    }

    public boolean submitEvent(MappedKeyEvent e) {
        return playerController.submitEvent(e);
    }

    public long getTimeTicks() {
        return timeTicks;
    }

    public EntityPlayer getPlayer() {
        return player;
    }

    public Entity getControlled() {
        return playerController.getControlled();
    }

    public void setControlled(Entity target) {
        getControlled().onControlLost();
        playerController = target.newController();
    }

    public void setController(Controller controller) {

        getControlled().setController(controller);
        playerController = controller;
    }

    public void setCamera(Entity target) {
        playerCamera = target.newCamera();
    }

    public List<Entity> parseSelector(String selector) {

        assert isSelector(selector);

        List<Entity> list = new ArrayList<>();

        char type = selector.charAt(1);
        switch (type) {

            case 'p':
                list.add(player);
                break;

            case 'c':
                list.add(getControlled());
                break;

            case 'e':
                String args = selector.substring(3, selector.lastIndexOf(']'));
                int index = Integer.parseInt(args);
                list.add(entities.get(index));
                break;

            case 'a':
                list.addAll(entities);
                list.add(player);
                break;
        }

        return list;
    }

    public Entity parseSelectorFirst(String selector) {

        return parseSelector(selector).get(0);
    }

    public void executeCommand(CommandSender sender, String commandStr) {

        if (commandStr.startsWith("/"))
            commandStr = commandStr.substring(1);

        String[] commandArgs = commandStr.split(" ");
        String commandID = commandArgs[0];
        commandArgs = Arrays.copyOfRange(commandArgs, 1, commandArgs.length);

        commandHandler.queueCommand(commandID, sender, commandArgs);
    }

    private enum ModificationType {
        ADD,
        REMOVE
    }

    private class DelayedEntityListModification {

        private final Entity entity;
        private final ModificationType type;

        private DelayedEntityListModification(Entity entity, ModificationType type) {
            this.entity = entity;
            this.type = type;
        }

        private void doModification() {

            switch (type) {

                case ADD:
                    Level.this.spawnEntityInternal(entity);
                    break;

                case REMOVE:
                    Level.this.removeEntityInternal(entity);
                    break;
            }
        }
    }
}