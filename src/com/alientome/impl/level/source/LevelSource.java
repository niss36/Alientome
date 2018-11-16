package com.alientome.impl.level.source;

import com.alientome.game.buffs.Buff;
import com.alientome.game.camera.Camera;
import com.alientome.game.control.Controller;
import com.alientome.game.entities.Entity;
import com.alientome.game.entities.EntityPlayer;
import com.alientome.game.level.Level;
import com.alientome.game.level.LevelMap;
import com.alientome.game.scripts.ScriptObject;
import com.alientome.game.background.ParallaxBackground;

import java.io.IOException;
import java.util.List;

public interface LevelSource {

    void load() throws IOException;

    void reset(Level level, List<Entity> entities, List<Buff> buffs);

    EntityPlayer newPlayer(Level level);

    Camera newCamera(Level level);

    Controller newController(Level level);

    LevelMap getMap();

    ParallaxBackground getBackground();

    List<ScriptObject> getScripts();
}
