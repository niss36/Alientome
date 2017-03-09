package com.gui;

import com.keybindings.InputManager;
import com.util.listeners.IntValueListener;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static com.util.Util.keycodeToString;

class GameKeyPicker extends GameButton {

    private final List<IntValueListener> listeners = new ArrayList<>();
    private final Object waitKeyPress = new Object();
    private int currentKeycode;
    private int keyPressed;
    private boolean inUse = false;

    GameKeyPicker(int defaultKeycode, Dimension dimension, Font font, Predicate<Integer> keyValidator) {
        super(dimension, "", font);

        setCurrentKey(defaultKeycode);

        InputManager.getInstance().addUnknownEventHandler("keySelect", event -> {
            if (inUse && event.getID() == KeyEvent.KEY_PRESSED) {
                int keyCode = event.getKeyCode();
                if (keyCode > 0 && (keyCode == KeyEvent.VK_ESCAPE || keyCode == currentKeycode || keyValidator.test(keyCode))) {
                    synchronized (waitKeyPress) {
                        keyPressed = keyCode;
                        waitKeyPress.notify();
                    }
                }
                return true;
            }
            return false;
        });

        addActionListener(e -> {
            String previousInputContext = InputManager.getInstance().getActiveContext();
            if (!inUse && !previousInputContext.equals("keySelect"))
                new Thread(() -> {
                    inUse = true;
                    InputManager.getInstance().setActiveContext("keySelect");
                    keyPressed = 0;

                    String previousText = getText();

                    setText("<...>");

                    synchronized (waitKeyPress) {
                        try {
                            waitKeyPress.wait();
                        } catch (InterruptedException ie) {
                            ie.printStackTrace();
                        }
                    }

                    if (keyPressed == currentKeycode)
                        setText(previousText);
                    else {
                        if (keyPressed == KeyEvent.VK_ESCAPE)
                            keyPressed = 0;

                        setCurrentKey(keyPressed);
                        for (IntValueListener listener : listeners)
                            listener.valueChanged(keyPressed);
                    }

                    inUse = false;
                    InputManager.getInstance().setActiveContext(previousInputContext);
                }).start();
        });
    }

    void setCurrentKey(int keyCode) {
        currentKeycode = keyCode;
        String keyName = keycodeToString(keyCode);
        setText(keyName);
    }

    void addValueListener(IntValueListener listener) {
        listeners.add(listener);
    }
}
