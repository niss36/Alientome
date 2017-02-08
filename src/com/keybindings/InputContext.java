package com.keybindings;

import com.util.listeners.InputListener;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class InputContext {

    final String id;
    private final InputContext parent;
    private final List<InputContext> children = new ArrayList<>();
    private final Map<String, KeyBinding> bindings = new HashMap<>();
    private final Map<String, Integer> bindingIDToKeycode = new HashMap<>();
    private final List<Function<KeyEvent, Boolean>> unknownEventHandlers = new ArrayList<>();

    private InputContext(String id, InputContext parent) {
        this.id = id;
        this.parent = parent;
    }

    static InputContext parseXML(InputManager manager, Element contextNode, InputContext parentContext) {

        String id = contextNode.getAttribute("id");

        InputContext context = new InputContext(id, parentContext);

        NodeList bindings = contextNode.getElementsByTagName("binding");
        for (int i = 0; i < bindings.getLength(); i++) {
            Element bindingNode = (Element) bindings.item(i);

            if (!bindingNode.getParentNode().equals(contextNode))
                continue;

            KeyBinding binding = KeyBinding.parseXML(manager, bindingNode, context);
            context.bindings.put(binding.id, binding);
        }

        manager.registerContext(context);

        NodeList childrenContexts = contextNode.getElementsByTagName("context");
        for (int i = 0; i < childrenContexts.getLength(); i++) {
            Element childContextNode = (Element) childrenContexts.item(i);

            if (!childContextNode.getParentNode().equals(contextNode))
                continue;

            InputContext childContext = parseXML(manager, childContextNode, context);
            context.children.add(childContext);
        }

        return context;
    }

    void setListener(String bindingID, InputListener listener) {
        bindings.get(bindingID).setListener(listener);
    }

    void addUnknownEventHandler(Function<KeyEvent, Boolean> handler) {
        unknownEventHandlers.add(handler);
    }

    private boolean handleUnknownEvent(KeyEvent e) {

        for (Function<KeyEvent,Boolean> handler : unknownEventHandlers)
            if (handler.apply(e))
                return true;

        return false;
    }

    boolean consumeEvent(KeyEvent e) {

        if (parent != null && parent.consumeEvent(e)) return true;

        String bindingID = null;

        for (Map.Entry<String, Integer> entry : bindingIDToKeycode.entrySet())
            if (entry.getValue() == e.getKeyCode()) {
                bindingID = entry.getKey();
                break;
            }

        if (bindingID == null) return handleUnknownEvent(e);

        MappedKeyEvent me = new MappedKeyEvent(bindingID, e);
        KeyBinding binding = bindings.get(bindingID);
        return binding != null && binding.consumeEvent(me);
    }

    public void setKeybinding(String bindingID, int keycode) {

        KeyBinding binding = bindings.get(bindingID);
        if (binding != null) {
            bindingIDToKeycode.put(bindingID, keycode);
            binding.notifyListeners(keycode);
        } else
            throw new IllegalArgumentException("Unknown binding ID : " + bindingID);
    }

    public int getKeybinding(String bindingID) {

        Integer keycode = bindingIDToKeycode.get(bindingID);
        return keycode != null ? keycode : KeyEvent.VK_UNDEFINED;
    }

    boolean isKeyBound(int keycode) {

        return isKeyBound(keycode, true, true);
    }

    private boolean isKeyBound(int keycode, boolean checkParent, boolean checkChildren) {

        if (checkParent && parent != null && parent.isKeyBound(keycode, true, false)) return true;

        if (checkChildren)
            for (InputContext child : children)
                if (child.isKeyBound(keycode, false, true)) return true;

        for (Integer value : bindingIDToKeycode.values())
            if (value == keycode) return true;

        return false;
    }

    void save(Map<String, Integer> values) {

        for (Map.Entry<String, Integer> entry : bindingIDToKeycode.entrySet())
            values.put(entry.getKey(), entry.getValue());
    }

    void read(List<String> lines) {

        for (String line : lines) {

            String[] keyValPair = line.split("=");
            String key = keyValPair[0];
            Integer value = Integer.valueOf(keyValPair[1]);
            KeyBinding binding = bindings.get(key);
            if (binding != null) {
                bindingIDToKeycode.put(key, value);
                binding.notifyListeners(value);
            }
        }
    }
}
