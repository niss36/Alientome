package com.keybindings;

import com.util.listeners.InputListener;
import com.util.listeners.IntValueListener;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;

public class KeyBinding {

    public final String id;
    public final InputContext context;
    private final List<IntValueListener> listeners = new ArrayList<>();
    private InputListener listener;

    private KeyBinding(String id, InputContext context) {
        this.id = id;
        this.context = context;
    }

    static KeyBinding parseXML(InputManager manager, Element bindingNode, InputContext context) {

        String id = bindingNode.getAttribute("id");

        boolean mappable = Boolean.parseBoolean(bindingNode.getAttribute("mappable"));

        KeyBinding keyBinding = new KeyBinding(id, context);

        if (mappable) manager.mappableBindings.add(keyBinding);

        return keyBinding;
    }

    void setListener(InputListener listener) {
        this.listener = listener;
    }

    boolean consumeEvent(MappedKeyEvent e) {
        return listener != null && listener.consumeEvent(e);
    }

    public void addValueListener(IntValueListener listener) {
        listeners.add(listener);
    }

    void notifyListeners(int newValue) {
        for (IntValueListener listener : listeners)
            listener.valueChanged(newValue);
    }
}
