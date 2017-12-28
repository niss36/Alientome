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
import com.alientome.script.arithmetic.ArithmeticValue;
import com.alientome.script.functions.VoidFunction;
import com.alientome.script.values.BooleanValue;
import com.alientome.script.values.IntegerValue;
import com.alientome.script.values.StringValue;

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
        engine.addBinding("loadLevel", (VoidFunction) (args, c) -> loadLevel((String) args[0].objValue()));

        engine.addBinding("freezePlayer", (VoidFunction) (args, c) -> level.getController().disable());
        engine.addBinding("unfreezePlayer", (VoidFunction) (args, c) -> level.getController().enable());

        engine.addBinding("travelCamera", (VoidFunction) (args, c) -> {
            ArithmeticValue x = (ArithmeticValue) args[0];
            ArithmeticValue y = (ArithmeticValue) args[1];
            IntegerValue time = (IntegerValue) args[2];
            level.setCamera(new TravelCamera(new Vec2(x.numValue().doubleValue(), y.numValue().doubleValue()), time.numValue()));
        });

        engine.addBinding("travelCameraPlayer", (VoidFunction) (args, c) -> {
            IntegerValue time = (IntegerValue) args[0];
            level.setCamera(new TravelCamera(level.getPlayer().getCenterPos(), time.numValue()));
        });

        engine.addBinding("playerCamera", (VoidFunction) (args, c) -> level.setCamera(level.getPlayer().newCamera()));

        engine.addBinding("playSound", (VoidFunction) (args, c) -> {
            String id = (String) args[0].objValue();
            BooleanValue loop = args.length > 1 ? (BooleanValue) args[1] : new BooleanValue(Boolean.FALSE);
            SoundManager manager = SharedInstances.get(SOUND_MANAGER);
            if (loop.objValue()) manager.playLooping(id);
            else manager.playOnce(id);
        });

        engine.addBinding("stopPlaying", (VoidFunction) (args, c) -> {
            String id = (String) args[0].objValue();
            SoundManager manager = SharedInstances.get(SOUND_MANAGER);
            manager.stopPlaying(id);
        });

        engine.addBinding("enableScript", (VoidFunction) (args, c) -> {
            String id = (String) args[0].objValue();
            level.setScriptEnabled(id, true);
        });

        engine.addBinding("disableScript", (VoidFunction) (args, c) -> {
            String id = (String) args[0].objValue();
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
