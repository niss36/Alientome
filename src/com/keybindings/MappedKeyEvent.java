package com.keybindings;

import java.awt.event.KeyEvent;

import static java.awt.event.KeyEvent.KEY_PRESSED;
import static java.awt.event.KeyEvent.KEY_RELEASED;

public class MappedKeyEvent {

    public final String bindingID;
    public final int eventID;

    MappedKeyEvent(String bindingID, KeyEvent e) {
        this.bindingID = bindingID;
        this.eventID = e.getID();

        if (!isKeyPressed() && !isKeyReleased()) System.err.println("What am I ? " + bindingID + eventID);
    }

    public boolean isKeyPressed() {
        return eventID == KEY_PRESSED;
    }

    public boolean isKeyReleased() {
        return eventID == KEY_RELEASED;
    }
}
