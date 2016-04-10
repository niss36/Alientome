package com.game;

import com.game.level.Level;
import com.gui.Panel;
import com.util.Direction;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class Game implements Runnable {

    public final ArrayList<Integer> pressedKeys = new ArrayList<>();
    private final Panel panel;
    private KeyEventDispatcher ked;

    boolean pause = false;
    String[] pauseChoices = {"Resume", "Reset", "Quit"};

    public Game(Panel p) {

        panel = p;

        KeyboardFocusManager.getCurrentKeyboardFocusManager().removeKeyEventDispatcher(ked);
        pressedKeys.clear();

        ked = new KeyEventDispatcher() {
            @Override
            public boolean dispatchKeyEvent(KeyEvent e) {

                if (e.getKeyCode() == KeyEvent.VK_H && e.getID() == KeyEvent.KEY_PRESSED) {
                    panel.switchDebug();
                    return true;
                }

                if (e.getKeyCode() == KeyEvent.VK_X && e.getID() == KeyEvent.KEY_PRESSED) {
                    Level.getInstance().player.throwGhostBall();
                    return true;
                }

                if(e.getKeyCode() == KeyEvent.VK_ESCAPE && e.getID() == KeyEvent.KEY_PRESSED) {
                    if(!pause) {
                        pause = true;
                        int i = JOptionPane.showOptionDialog(null, "", "Game paused", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, null, pauseChoices, pauseChoices[0]);
                        if (i == JOptionPane.NO_OPTION) Level.getInstance().reset();
                        else if (i == JOptionPane.CANCEL_OPTION) System.exit(0);
                        pause = false;
                    }
                    return true;
                }

                if (e.getID() == KeyEvent.KEY_PRESSED && !pressedKeys.contains(e.getKeyCode()))
                    pressedKeys.add(e.getKeyCode());
                else if (e.getID() == KeyEvent.KEY_RELEASED)
                    pressedKeys.remove((Integer) e.getKeyCode());

                return e.getKeyCode() != KeyEvent.VK_SPACE || Direction.toDirection(e.getKeyCode()) != null;
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

            if(!pause) Level.getInstance().update(this, panel);
        }
    }
}
