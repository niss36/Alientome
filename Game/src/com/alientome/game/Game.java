package com.alientome.game;

import com.alientome.core.SharedInstances;
import com.alientome.core.events.GameEventDispatcher;
import com.alientome.core.keybindings.InputManager;
import com.alientome.core.keybindings.InputListener;
import com.alientome.core.util.Logger;
import com.alientome.game.events.GameErrorEvent;
import com.alientome.game.events.GamePauseEvent;
import com.alientome.game.events.GameResumeEvent;
import com.alientome.game.events.GameStartEvent;
import com.alientome.game.level.Level;
import com.alientome.game.level.LevelManager;

import static com.alientome.core.SharedNames.DISPATCHER;
import static com.alientome.core.SharedNames.INPUT_MANAGER;
import static com.alientome.core.events.GameEventType.*;
import static com.alientome.core.util.Util.makeListener;

public class Game implements Runnable {

    private static final Logger log = Logger.get();
    private static final int SKIP_TICKS = 30;
    private static final int MAX_FRAME_SKIP = 5;
    private final Object pausedWaitLock = new Object();
    private final Object untilPauseWaitLock = new Object();
    private final GameRenderer renderer;
    private long updates;
    private long averageUpdateTime;
    private LevelManager manager;
    private boolean updating = false;
    private State state = null;
    private boolean run = false;

    public Game(GameRenderer renderer) {

        this.renderer = renderer;

        GameEventDispatcher dispatcher = SharedInstances.get(DISPATCHER);

        dispatcher.register(GAME_START, e -> {
            dispatcher.submit(new GameResumeEvent());
            run = true;
            manager = ((GameStartEvent) e).manager;
            manager.reset();
            new Thread(this, "Thread-Game").start();
        });

        dispatcher.register(GAME_EXIT, e -> {
            setState(null);
            run = false;
            synchronized (pausedWaitLock) {
                pausedWaitLock.notify();
            }
            synchronized (untilPauseWaitLock) {
                untilPauseWaitLock.notify();
            }
        });

        dispatcher.register(GAME_PAUSE, e -> {
            setState(State.PAUSED);
            synchronized (untilPauseWaitLock) {
                updating = false;
                try {
                    untilPauseWaitLock.wait();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        });

        dispatcher.register(GAME_RESUME, e -> {
            setState(State.RUNNING);
            if (!updating)
                synchronized (pausedWaitLock) {
                    updating = true;
                    pausedWaitLock.notify();
                }
        });

        dispatcher.register(GAME_RESET, e -> {
            dispatcher.submit(new GameResumeEvent());
            manager.reset();
        });

        dispatcher.register(GAME_DEATH, e -> setState(State.DEATH));

        InputManager manager = SharedInstances.get(INPUT_MANAGER);

        manager.setListener("running", "pause", makeListener(() -> dispatcher.submit(new GamePauseEvent())));

        InputListener levelListener = e -> getLevel().submitEvent(e);

        manager.setListener("running", "moveLeft", levelListener);
        manager.setListener("running", "moveRight", levelListener);
        manager.setListener("running", "jump", levelListener);
        manager.setListener("running", "special1", levelListener);
        manager.setListener("running", "special2", levelListener);
        manager.setListener("running", "special3", levelListener);
    }

    public boolean isPaused() {
        return state == State.PAUSED;
    }

    public Level getLevel() {
        return manager.getLevel();
    }

    public DebugInfo getDebugInfo() {
        return getLevel().getDebugInfo();
    }

    public long getAverageUpdateTime() {
        return averageUpdateTime;
    }

    private void setState(State newState) {
        state = newState;
        if (state != null) {
            InputManager manager = SharedInstances.get(INPUT_MANAGER);
            manager.setActiveContext(state.inputContext);
        }
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

                        getLevel().update();

                        long updateTime = System.nanoTime() - updateStart;

                        averageUpdateTime += (updateTime - averageUpdateTime) / ++updates;

                        nextTick += SKIP_TICKS;
                        loops++;
                    }

                    interpolation = (double) (System.currentTimeMillis() + SKIP_TICKS - nextTick) / SKIP_TICKS;
                    renderer.render(interpolation);

                } else {

                    synchronized (untilPauseWaitLock) {
                        untilPauseWaitLock.notify();
                    }

                    long beforeWait = System.currentTimeMillis();

                    synchronized (pausedWaitLock) {
                        pausedWaitLock.wait();
                    }
                    long waited = System.currentTimeMillis() - beforeWait;
                    nextTick += waited;
                }
            }

        } catch (Exception e) {
            setState(State.ERROR);
            log.e("An exception has occurred : ");
            e.printStackTrace();
            GameEventDispatcher dispatcher = SharedInstances.get(DISPATCHER);
            dispatcher.submit(new GameErrorEvent(e));

        } finally {

            manager.dispose();
            manager = null;

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
