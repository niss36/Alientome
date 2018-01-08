package com.alientome.impl.level;

import com.alientome.core.SharedInstances;
import com.alientome.core.events.GameEventDispatcher;
import com.alientome.core.events.GameEventListener;
import com.alientome.core.events.GameEventType;
import com.alientome.core.sound.SoundManager;
import com.alientome.core.util.Logger;
import com.alientome.core.util.Vec2;
import com.alientome.game.camera.TravelCamera;
import com.alientome.game.level.Level;
import com.alientome.game.level.LevelManager;
import com.alientome.script.ScriptEngine;
import com.alientome.script.ScriptParser;
import com.alientome.script.functions.VoidFunction;
import javafx.beans.property.Property;

import static com.alientome.core.SharedNames.DISPATCHER;
import static com.alientome.core.SharedNames.SOUND_MANAGER;

public abstract class AbstractLevelManager implements LevelManager {

    protected static final Logger log = Logger.get();
    protected final ScriptEngine engine = new ScriptEngine();
    protected final ScriptParser parser = engine.newParser();
    protected Level level;

    protected AbstractLevelManager() {

        GameEventListener listener = e -> getLevel().onPaused();

        GameEventDispatcher dispatcher = SharedInstances.get(DISPATCHER);
        dispatcher.register(GameEventType.GAME_PAUSE, listener);

        dispatcher.addToCache(this, listener);

        engine.addBinding("nextLevel", (VoidFunction) (args, c) -> nextLevel());
        engine.addBinding("loadLevel", (VoidFunction) (args, c) -> loadLevel((String) args[0]));

        engine.addBinding("freezePlayer", (VoidFunction) (args, c) -> level.getController().disable());
        engine.addBinding("unfreezePlayer", (VoidFunction) (args, c) -> level.getController().enable());

        engine.addBinding("travelCamera", (VoidFunction) (args, c) -> {
            Number x = (Number) args[0];
            Number y = (Number) args[1];
            Integer time = (Integer) args[2];
            level.setCamera(new TravelCamera(new Vec2(x.doubleValue(), y.doubleValue()), time));
        });

        engine.addBinding("travelCameraPlayer", (VoidFunction) (args, c) -> {
            Integer time = (Integer) args[0];
            level.setCamera(new TravelCamera(level.getPlayer().getCenterPos(), time));
        });

        engine.addBinding("playerCamera", (VoidFunction) (args, c) -> level.setCamera(level.getPlayer().newCamera()));

        Property<SoundManager> manager = SharedInstances.getProperty(SOUND_MANAGER);

        engine.addBinding("playSound", (VoidFunction) (args, c) -> {
            String id = (String) args[0];
            boolean loop = args.length > 1 && (Boolean) args[1];
            if (loop) manager.getValue().playLooping(id);
            else manager.getValue().playOnce(id);
        });

        engine.addBinding("stopPlaying", (VoidFunction) (args, c) -> {
            String id = (String) args[0];
            manager.getValue().stopPlaying(id);
        });

        engine.addBinding("enableScript", (VoidFunction) (args, c) -> {
            String id = (String) args[0];
            level.setScriptEnabled(id, true);
        });

        engine.addBinding("disableScript", (VoidFunction) (args, c) -> {
            String id = (String) args[0];
            level.setScriptEnabled(id, false);
        });
    }

    @Override
    public void dispose() {

        GameEventDispatcher dispatcher = SharedInstances.get(DISPATCHER);
        dispatcher.removeFromCache(this);
    }

    @Override
    public void reset() {
        level.reset();
    }

    @Override
    public Level getLevel() {
        return level;
    }
}
