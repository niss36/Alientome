package com.alientome.core.keybindings;

import javafx.beans.property.Property;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.io.File;

public interface InputManager {

    void load();

    boolean needsSave();

    void save();

    void reset();

    void createKeybindingsFile(File targetFile);

    void setListener(String contextID, String bindingID, InputListener listener);

    Property<KeyCode> bindingProperty(String contextID, String bindingID);

    boolean isBound(String contextID, KeyCode code);

    boolean consumeEvent(KeyEvent e);

    void setActiveContext(String contextID);
}
