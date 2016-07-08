package com.gui;

import com.util.Config;
import com.util.listeners.ConfigListener;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.lang.reflect.Field;

class MenuKeyButton extends MenuButton implements ActionListener, KeyListener, ConfigListener {

    public static boolean inUse;
    private final int index;
    private int currentKeyCode;
    private String currentKeyName;
    private int keyPressed;
    private boolean space;

    public MenuKeyButton(Dimension dimension, int index) {
        super("", dimension);

        inUse = false;
        this.index = index;

        setCurrentKey(Config.getInstance().getKey(index));

        addActionListener(this);

        setFocusable(true);

        addKeyListener(this);

        Config.getInstance().addConfigListener(this);
    }

    private String keyCodeToString(int keyCode) {

        for (Field f : KeyEvent.class.getFields()) {

            if (f.getName().substring(0, 3).equals("VK_")) {
                try {
                    if ((Integer) f.get(null) == keyCode) {
                        return f.getName().substring(3);
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

    private void setCurrentKey(int keyCode) {
        currentKeyCode = keyCode;
        setText(keyCodeToString(keyCode));
        currentKeyName = getText();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        requestFocus();
        if (!inUse && !space)
            new Thread(() -> {
                inUse = true;
                keyPressed = 0;

                setText("<...>");

                while (keyPressed == 0 ||
                        (Config.getInstance().isKeyUsed(keyPressed)
                                && keyPressed != currentKeyCode
                                && keyPressed != KeyEvent.VK_ESCAPE)
                        )
                    requestFocus();

                if (keyPressed == KeyEvent.VK_ESCAPE || keyPressed == currentKeyCode) {
                    setText(currentKeyName);
                    inUse = false;
                    Frame.getInstance().panelGame.showCard(PanelGame.MENU_CONTROLS);
                    return;
                }
                setCurrentKey(keyPressed);
                Config.getInstance().setKey(index, currentKeyName, currentKeyCode);
                inUse = false;
                Frame.getInstance().panelGame.showCard(PanelGame.MENU_CONTROLS);
            }).start();
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (inUse) {
            if (e.getModifiers() != 0) {
                keyPressed = 0;
                e.consume();
            } else keyPressed = e.getKeyCode();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (inUse) space = e.getKeyCode() == KeyEvent.VK_SPACE;
    }

    @Override
    public void configReset() {
        configKeysReset();
    }

    @Override
    public void configKeysReset() {
        setCurrentKey(Config.getInstance().getKey(index));
    }
}
