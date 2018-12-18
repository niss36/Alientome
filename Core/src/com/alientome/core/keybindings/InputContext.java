package com.alientome.core.keybindings;

import com.alientome.core.util.WrappedXML;
import javafx.beans.InvalidationListener;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InputContext {

    protected final String id;
    private final InputContext parent;
    private final List<InputContext> children = new ArrayList<>();
    private final Map<String, KeyBinding> bindings = new HashMap<>();
    private final Map<String, Property<KeyCode>> bindingIDToKeycode = new HashMap<>();

    private InputContext(String id, InputContext parent) {
        this.id = id;
        this.parent = parent;
    }

    public static InputContext parseXML(AbstractInputManager manager, WrappedXML contextXML, InputContext parentContext) {

        String id = contextXML.getAttr("id");

        InputContext context = new InputContext(id, parentContext);

        for (WrappedXML bindingXML : contextXML.nodesWrapped("binding")) {

            KeyBinding binding = KeyBinding.parseXML(bindingXML, context);
            context.bindings.put(binding.id, binding);
        }

        manager.registerContext(context);

        for (WrappedXML childContextXML : contextXML.nodesWrapped("context")) {

            InputContext childContext = parseXML(manager, childContextXML, context);
            context.children.add(childContext);
        }

        return context;
    }

    protected void setListener(String bindingID, InputListener listener) {
        bindings.get(bindingID).setListener(listener);
    }

    protected boolean consumeEvent(KeyEvent e) {

        if (parent != null && parent.consumeEvent(e)) return true;

        String bindingID = null;

        for (Map.Entry<String, Property<KeyCode>> entry : bindingIDToKeycode.entrySet())
            if (entry.getValue().getValue() == e.getCode()) {
                bindingID = entry.getKey();
                break;
            }

        if (bindingID == null) return false;

        MappedKeyEvent me = new MappedKeyEvent(bindingID, e);
        KeyBinding binding = bindings.get(bindingID);
        return binding != null && binding.consumeEvent(me);
    }

    public Property<KeyCode> bindingProperty(String bindingID) {
        return bindingIDToKeycode.get(bindingID);
    }

    public boolean isBound(KeyCode code) {

        return isBound(code, true, true);
    }

    protected boolean isBound(KeyCode code, boolean checkParent, boolean checkChildren) {

        if (checkParent && parent != null && parent.isBound(code, true, false)) return true;

        if (checkChildren)
            for (InputContext child : children)
                if (child.isBound(code, false, true)) return true;

        for (Property<KeyCode> property : bindingIDToKeycode.values())
            if (property.getValue() == code) return true;

        return false;
    }

    protected void save(Map<String, String> values) {

        for (Map.Entry<String, Property<KeyCode>> entry : bindingIDToKeycode.entrySet())
            values.put(entry.getKey(), entry.getValue().getValue().name());
    }

    protected void read(List<String> lines, InvalidationListener invalidationListener) {

        for (String line : lines) {

            String[] keyValPair = line.split("=");
            String key = keyValPair[0];
            KeyCode value = KeyCode.valueOf(keyValPair[1]);
            KeyBinding binding = bindings.get(key);
            if (binding != null)
                bindingIDToKeycode.computeIfAbsent(key, s -> newProperty(invalidationListener)).setValue(value);
        }

        for (KeyBinding binding : bindings.values())
            bindingIDToKeycode.computeIfAbsent(binding.id, s -> newProperty(invalidationListener));
    }

    private Property<KeyCode> newProperty(InvalidationListener listener) {
        Property<KeyCode> property = new SimpleObjectProperty<>(KeyCode.UNDEFINED);
        property.addListener(listener);
        return property;
    }
}
