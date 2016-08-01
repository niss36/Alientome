package com.game;

import com.game.level.Level;
import com.gui.Frame;
import com.gui.PanelGame;
import com.util.Config;
import com.util.Direction;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

/**
 * <code>Runnable</code> object used to perform game updates.
 */
public final class Game implements Runnable {

    private static final Game ourInstance = new Game();
    private final ArrayList<Integer> pressedKeys = new ArrayList<>();
    public State state;
    private PanelGame panelGame;
    private boolean run = true;

    private Game() {

        KeyEventDispatcher ked = e -> {

            if (e.getKeyCode() == Config.getInstance().getInt("Key.Pause") && e.getID() == KeyEvent.KEY_PRESSED) {
                if (state == State.PAUSED) {
                    resume();
                    return true;
                }

                if (state == State.RUNNING) {
                    pause();
                    return true;
                }
            }

            if (state != State.RUNNING) return false;

            if (e.getKeyCode() == Config.getInstance().getInt("Key.Debug") && e.getID() == KeyEvent.KEY_PRESSED) {
                panelGame.switchDebug();
                return true;
            }

            if (e.getKeyCode() == Config.getInstance().getInt("Key.Fire")) {
                if (e.getID() == KeyEvent.KEY_PRESSED) Level.getInstance().player.startCharging();
                else if (e.getID() == KeyEvent.KEY_RELEASED) Level.getInstance().player.stopCharging();
                return true;
            }

            if (e.getID() == KeyEvent.KEY_PRESSED && !pressedKeys.contains(e.getKeyCode()))
                synchronized (pressedKeys) {
                    pressedKeys.add(e.getKeyCode());
                }
            else if (e.getID() == KeyEvent.KEY_RELEASED)
                synchronized (pressedKeys) {
                    pressedKeys.remove((Integer) e.getKeyCode());
                }

            return e.getKeyCode() != Config.getInstance().getInt("Key.Jump") || Direction.toDirection(e.getKeyCode()) != null;
        };

        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(ked);
    }

    public static Game getInstance() {
        return ourInstance;
    }

    public void init(PanelGame panel) {

        state = null;

        panelGame = panel;
    }

    public void start() {

        state = State.RUNNING;

        run = true;

        pressedKeys.clear();

        Level.getInstance().reset();

        new Thread(this, "Thread-Game").start();
    }

    public void exit() {

        Level.getInstance().save();
        Frame.getInstance().showCard(Frame.MENU);

        state = null;

        run = false;
    }

    public void pause() {
        if (state == State.RUNNING) panelGame.showCard(PanelGame.MENU);
    }

    public void resume() {

        panelGame.showCard(PanelGame.BLANK);
        pressedKeys.clear();
    }

    public void reset() {

        resume();
        Level.getInstance().reset();
    }

    public void playerDeath() {
        panelGame.showCard(PanelGame.DEATH);
    }

    @Override
    public void run() {

        while (run) {

            try {
                Thread.sleep(30);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (state == State.RUNNING)
                synchronized (pressedKeys) { //Prevent concurrent modification exception
                    Level.getInstance().update(pressedKeys, panelGame);
                }
        }
    }

    public enum State {

        RUNNING,
        PAUSED,
        PAUSED_CONTROLS,
        PAUSED_OPTIONS,
        DEATH
    }
}
