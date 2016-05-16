package com.game;

import com.gui.Panel;
import com.util.Config;
import com.util.Direction;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

/**
 * <code>Runnable</code> object used to perform game updates.
 */
public class Game implements Runnable {

    public final ArrayList<Integer> pressedKeys = new ArrayList<>();
    private final Panel panel;
    public State state = State.RUNNING;
    private KeyEventDispatcher ked;

    private boolean run = true;

    /**
     * Initialize the <code>Game</code>
     *
     * @param p the <code>Panel</code> to update
     */
    public Game(Panel p) {

        panel = p;

        KeyboardFocusManager.getCurrentKeyboardFocusManager().removeKeyEventDispatcher(ked);
        pressedKeys.clear();

        ked = new KeyEventDispatcher() {
            @Override
            public boolean dispatchKeyEvent(KeyEvent e) {

                if (e.getKeyCode() == Config.getInstance().getKey("Key.Pause") && e.getID() == KeyEvent.KEY_PRESSED) {
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

                if (e.getKeyCode() == Config.getInstance().getKey("Key.Debug") && e.getID() == KeyEvent.KEY_PRESSED) {
                    panel.switchDebug();
                    return true;
                }

                if (e.getKeyCode() == Config.getInstance().getKey("Key.Fire")) {
                    if (e.getID() == KeyEvent.KEY_PRESSED) Level.getInstance().player.startCharging();
                    else if (e.getID() == KeyEvent.KEY_RELEASED) Level.getInstance().player.stopCharging();
                    return true;
                }

                if (e.getID() == KeyEvent.KEY_PRESSED && !pressedKeys.contains(e.getKeyCode()))
                    pressedKeys.add(e.getKeyCode());
                else if (e.getID() == KeyEvent.KEY_RELEASED)
                    pressedKeys.remove((Integer) e.getKeyCode());

                return e.getKeyCode() != Config.getInstance().getKey("Key.Jump") || Direction.toDirection(e.getKeyCode()) != null;
            }
        };

        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(ked);

        Level.getInstance().reset();
    }

    public void pause() {
        if (state == State.RUNNING) panel.showCard(Panel.MENU);
    }

    public void resume() {

        panel.showCard(Panel.BLANK);
        pressedKeys.clear();
    }

    public void reset() {

        resume();
        Level.getInstance().reset();
    }

    public void quit() {

        resume();
        run = false;
        System.exit(0);
    }

    public void playerDeath() {
        panel.showCard(Panel.DEATH);
    }

    @Override
    public void run() {

        while (run) {

            try {
                Thread.sleep(30);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (state == State.RUNNING) Level.getInstance().update(this, panel);
        }
    }

    public enum State {

        RUNNING,
        PAUSED,
        PAUSED_OPTIONS,
        DEATH
    }
}
