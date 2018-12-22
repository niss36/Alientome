package com.alientome.core.keybindings;

import javafx.beans.property.Property;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.io.IOException;
import java.nio.file.Path;

public interface InputManager {

    void load() throws IOException;

    boolean needsSave();

    void save() throws IOException;

    void reset() throws IOException;

    void createDefaultFile(Path target) throws IOException;

    void setListener(String contextID, String bindingID, InputListener listener);

    Property<KeyCode> bindingProperty(String contextID, String bindingID);

    boolean isBound(String contextID, KeyCode code);

    boolean consumeEvent(KeyEvent e);

    void setActiveContext(String contextID);
}
