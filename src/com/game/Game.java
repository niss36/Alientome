package com.game;

import com.events.GameEventDispatcher;
import com.game.level.Level;
import com.gui.InfoDialog;
import com.keybindings.InputManager;
import com.util.DebugInfo;
import com.util.Logger;
import com.util.listeners.InputListener;

import java.awt.*;

import static com.events.GameEventType.*;
import static com.util.Util.makeListener;

/**
 * <code>Runnable</code> object used to perform game updates.
 */
public class Game implements Runnable {

    private static final Logger log = Logger.get();
    private static final int SKIP_TICKS = 30;
    private static final int MAX_FRAME_SKIP = 5;
    private final Object waitLock = new Object();
    private final GameRenderer renderer;
    private long updates;
    private long averageUpdateTime;
    private Level level;
    private boolean updating = false;
    private State state = null;
    private boolean run = false;

    public Game(GameRenderer renderer) {

        this.renderer = renderer;

        GameEventDispatcher dispatcher = GameEventDispatcher.getInstance();

        dispatcher.register(GAME_START, e -> {
            dispatcher.submit(null, GAME_RESUME);
            run = true;
            level = (Level) e.source;
            level.reset();
            new Thread(this, "Thread-Game").start();
        });
        dispatcher.register(GAME_EXIT, e -> {
            setState(null);
            run = false;
            synchronized (waitLock) {
                waitLock.notify();
            }
        });
        dispatcher.register(GAME_PAUSE, e -> {
            setState(State.PAUSED);
            updating = false;
        });
        dispatcher.register(GAME_RESUME, e -> {
            setState(State.RUNNING);
            if (!updating)
                synchronized (waitLock) {
                    updating = true;
                    waitLock.notify();
                }
        });
        dispatcher.register(GAME_RESET, e -> {
            dispatcher.submit(null, GAME_RESUME);
            level.reset();
        });
        dispatcher.register(GAME_DEATH, e -> setState(State.DEATH));

        InputManager manager = InputManager.getInstance();

        manager.setListener("running", "pause", makeListener(() -> dispatcher.submit(null, GAME_PAUSE)));

        InputListener levelListener = e -> level.submitEvent(e);

        manager.setListener("running", "moveLeft", levelListener);
        manager.setListener("running", "moveRight", levelListener);
        manager.setListener("running", "jump", levelListener);
        manager.setListener("running", "special1", levelListener);
        manager.setListener("running", "special2", levelListener);
        manager.setListener("running", "special3", levelListener);

        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(manager::consumeEvent);
    }

    public boolean isRunning() {
        return state == State.RUNNING;
    }

    public boolean isPaused() {
        return state == State.PAUSED;
    }

    public Level getLevel() {
        return level;
    }

    public DebugInfo getDebugInfo() {
        return level.getDebugInfo();
    }

    public long getAverageUpdateTime() {
        return averageUpdateTime;
    }

    private void setState(State newState) {
        state = newState;
        if (state != null)
            InputManager.getInstance().setActiveContext(state.inputContext);
    }

    @Override
    public void run() {

        try {

            long nextTick = System.currentTimeMillis();
            int loops;
            double interpolation;

            while (run) {

                if (updating) {

                    loops = 0;

                    while (System.currentTimeMillis() > nextTick && loops < MAX_FRAME_SKIP) {

                        long updateStart = System.nanoTime();

                        level.update();

                        long updateTime = System.nanoTime() - updateStart;

                        averageUpdateTime += (updateTime - averageUpdateTime) / ++updates;

                        nextTick += SKIP_TICKS;
                        loops++;
                    }

                    interpolation = (double) (System.currentTimeMillis() + SKIP_TICKS - nextTick) / SKIP_TICKS;
                    renderer.render(interpolation);

                } else {

                    long beforeWait = System.currentTimeMillis();

                    synchronized (waitLock) {
                        waitLock.wait();
                    }
                    long waited = System.currentTimeMillis() - beforeWait;
                    nextTick += waited;
                }
            }

        } catch (Exception e) {
            setState(State.ERROR);
            log.e("An exception has occurred : ");
            e.printStackTrace();
            new InfoDialog("game.error.header", e.toString(), "game.error.label").showDialog();
            GameEventDispatcher.getInstance().submit(null, GAME_EXIT);

        } finally {

            level.save();
            level = null;

            System.gc();
        }
    }

    private enum State {

        RUNNING("running"),
        PAUSED("menu.pause"),
        DEATH("menu.death"),
        ERROR("error");

        private final String inputContext;

        State(String inputContext) {

            this.inputContext = inputContext;
        }
    }
}
