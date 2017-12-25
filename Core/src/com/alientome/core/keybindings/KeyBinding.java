package com.alientome.core.keybindings;

import com.alientome.core.util.WrappedXML;

public class KeyBinding {

    public final String id;
    public final InputContext context;
    private InputListener listener;

    private KeyBinding(String id, InputContext context) {
        this.id = id;
        this.context = context;
    }

    static KeyBinding parseXML(WrappedXML bindingXML, InputContext context) {

        String id = bindingXML.getAttr("id");

        return new KeyBinding(id, context);
    }

    void setListener(InputListener listener) {
        this.listener = listener;
    }

    boolean consumeEvent(MappedKeyEvent e) {
        return listener != null && listener.consumeEvent(e);
    }
}
