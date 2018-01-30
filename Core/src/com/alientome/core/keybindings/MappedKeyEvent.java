package com.alientome.core.keybindings;


import javafx.scene.input.KeyEvent;

public class MappedKeyEvent {

    public final String bindingID;
    public final boolean pressed;
    public final boolean released;

    MappedKeyEvent(String bindingID, KeyEvent e) {
        this.bindingID = bindingID;
        pressed = e.getEventType() == KeyEvent.KEY_PRESSED;
        released = e.getEventType() == KeyEvent.KEY_RELEASED;
    }
}
