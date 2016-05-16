package com.gui;

import com.util.Config;
import com.util.ConfigListener;

import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.Field;

class MenuKeyButton extends MenuButton implements ActionListener, KeyListener, ConfigListener {

    public static boolean inUse;
    private final Component parent;
    private final int index;
    private int currentKeyCode;
    private String currentKeyName;
    private int keyPressed;
    private boolean space;

    public MenuKeyButton(Component parent, Dimension dimension, int xCenterOffset, int yCenterOffset, int index) {
        super(parent, "", dimension, xCenterOffset, yCenterOffset);

        inUse = false;

        this.parent = parent;
        this.index = index;

        setCurrentKey(Config.getInstance().getKey(index));

        addActionListener(this);

        setFocusable(false);

        parent.addKeyListener(this);
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
        parent.requestFocus();
        if (!inUse && !space)
            new Thread(() -> {
                inUse = true;
                keyPressed = 0;

                setText("<...>");

                while (keyPressed == 0 ||
                        (Config.getInstance().isUsed(keyPressed)
                                && keyPressed != currentKeyCode
                                && keyPressed != KeyEvent.VK_ESCAPE)
                        )
                    parent.requestFocus();

                if (keyPressed == KeyEvent.VK_ESCAPE || keyPressed == currentKeyCode) {
                    setText(currentKeyName);
                    inUse = false;
                    return;
                }
                setCurrentKey(keyPressed);
                Config.getInstance().updatePropertiesAndKeyMap(index, currentKeyName, currentKeyCode);
                inUse = false;
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

        setCurrentKey(Config.getInstance().getKey(index));
    }
}
