package com.alientome.game.level;

import com.alientome.core.collisions.AxisAlignedBoundingBox;
import com.alientome.core.keybindings.MappedKeyEvent;
import com.alientome.game.DebugInfo;
import com.alientome.game.GameObject;
import com.alientome.game.blocks.Block;
import com.alientome.game.camera.Camera;
import com.alientome.game.commands.CommandSender;
import com.alientome.game.commands.exceptions.CommandException;
import com.alientome.game.control.Controller;
import com.alientome.game.entities.Entity;
import com.alientome.game.entities.EntityPlayer;
import com.alientome.game.particles.Particle;
import com.alientome.game.util.Selector;

import java.awt.*;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

public interface Level {

    void reset();

    void update();

    void onPaused();

    void spawnEntity(Entity entity);

    void removeEntity(Entity entity);

    void spawnParticle(Particle particle);

    void removeParticle(Particle particle);

    void draw(Graphics g, boolean debug, double interpolation);

    List<GameObject> getObjectsInRange(Class<? extends GameObject> type, double x, double y, double range, GameObject... excluded);

    int countObjectsInRange(Class<? extends GameObject> type, double x, double y, double range, GameObject... excluded);

    void forEachCollidableBlock(AxisAlignedBoundingBox area, Consumer<Block> action);

    void forEachCollidableEntity(AxisAlignedBoundingBox area, Consumer<Entity> action, Entity excluded);

    void applyScripts(Entity entity);

    boolean sightTest(Entity entity0, Entity entity1);

    boolean submitEvent(MappedKeyEvent e);

    List<Entity> selectAll(Selector selector) throws CommandException;

    Entity selectFirst(Selector selector) throws CommandException;

    void executeCommand(CommandSender sender, String command);

    Random getRandom();

    DebugInfo getDebugInfo();

    LevelMap getMap();

    long getTimeTicks();

    EntityPlayer getPlayer();

    Entity getControlled();

    void setControlled(Entity target);

    void setController(Controller controller);

    Controller getController();

    void setCamera(Camera camera);
}
