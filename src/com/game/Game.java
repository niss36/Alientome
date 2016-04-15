package com.game;

import com.game.level.Level;
import com.gui.MenuDialog;
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

    private KeyEventDispatcher ked;
    private boolean pause = false;

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

                if (pause) return false;

                if (e.getKeyCode() == Config.getInstance().getKey("Key.Debug") && e.getID() == KeyEvent.KEY_PRESSED) {
                    panel.switchDebug();
                    return true;
                }

                if (e.getKeyCode() == Config.getInstance().getKey("Key.Fire")) {
                    if (e.getID() == KeyEvent.KEY_PRESSED) Level.getInstance().player.startCharging();
                    else if (e.getID() == KeyEvent.KEY_RELEASED) Level.getInstance().player.stopCharging();
                    return true;
                }

                if (e.getKeyCode() == Config.getInstance().getKey("Key.Pause") && e.getID() == KeyEvent.KEY_PRESSED) {
                    pause = true;
                    int i = new MenuDialog(null, "Game paused", true).showDialog(); //JOptionPane.showOptionDialog(null, "", "Game paused", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, null, pauseChoices, pauseChoices[0]);
                    if (i == MenuDialog.RESET /*JOptionPane.NO_OPTION*/) Level.getInstance().reset();
                    else if (i == MenuDialog.QUIT /*JOptionPane.CANCEL_OPTION*/) System.exit(0);
                    pause = false;

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

    @Override
    public void run() {

        while (true) {

            try {
                Thread.sleep(30);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (!pause) Level.getInstance().update(this, panel);
        }
    }
}
