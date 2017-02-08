package com.keybindings;

import com.events.GameEventDispatcher;
import com.events.GameEventType;
import com.util.FileManager;
import com.util.Logger;
import com.util.Util;
import com.util.listeners.InputListener;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.awt.event.KeyEvent;
import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.util.profile.ExecutionTimeProfiler.theProfiler;

public class InputManager {
    private static final InputManager ourInstance = new InputManager();
    private static final Logger log = Logger.get();
    final List<KeyBinding> mappableBindings = new ArrayList<>();
    private final Map<String, InputContext> contexts = new LinkedHashMap<>();
    private final File userKeybindings;
    private InputContext activeContext;

    private InputManager() {
        userKeybindings = FileManager.getInstance().getKeybindings();

        GameEventDispatcher.getInstance().register(GameEventType.KEYBINDINGS_RESET, e -> reset());
    }

    public static InputManager getInstance() {
        return ourInstance;
    }

    public void load() {

        log.i("Loading keybindings");
        try {
            init();
        } catch (Exception e) {
            log.f("Error while loading key bindings : " + e.getMessage());
        }

        read(defaultKeybindings());

        read(userKeybindings);
        log.i("Loaded keybindings");
    }

    private void read(InputStream stream) {

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
            List<String> lines = reader.lines().collect(Collectors.toList());

            for (InputContext context : contexts.values()) context.read(lines);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void read(File file) {

        try {
            List<String> lines = Files.readAllLines(file.toPath());

            for (InputContext context : contexts.values()) context.read(lines);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void save() {

        log.i("Saving keybindings");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(userKeybindings))) {
            Map<String, Integer> values = new HashMap<>();
            for (InputContext context : contexts.values()) context.save(values);
            for (Map.Entry<String, Integer> entry : values.entrySet()) {

                String line = entry.getKey() + "=" + entry.getValue();

                writer.write(line);
                writer.newLine();
            }
            log.i("Saved keybindings");
        } catch (IOException e) {
            e.printStackTrace();
            log.e("Could not save keybindings");
        }
    }

    public void createKeybindingsFile(File targetFile) {

        try (InputStream stream = defaultKeybindings()) {
            Files.copy(stream, targetFile.toPath());
            log.i("Keybindings file was created");
        } catch (IOException e) {
            e.printStackTrace();
            log.w("Keybindings file could not be created");
        }
    }

    private void reset() {
        read(defaultKeybindings());
    }

    private InputStream defaultKeybindings() {
        return ClassLoader.getSystemResourceAsStream("defaultKeybindings.txt");
    }

    public void setListener(String contextID, String bindingID, InputListener listener) {
        contexts.get(contextID).setListener(bindingID, listener);
    }

    public void addUnknownEventHandler(String contextID, Function<KeyEvent, Boolean> handler) {
        contexts.get(contextID).addUnknownEventHandler(handler);
    }

    public boolean consumeEvent(KeyEvent e) {
        theProfiler.startSection("Keybindings");

        boolean ret = false;

        if (activeContext != null && activeContext.consumeEvent(e)) ret = true;

        theProfiler.endSection("Keybindings");
        return ret;
    }

    public List<KeyBinding> getMappableBindings() {
        return mappableBindings;
    }

    public boolean isKeyBound(int keycode, InputContext context) {
        return keycode > 0 && context.isKeyBound(keycode);
    }

    public String getActiveContext() {
        return activeContext != null ? activeContext.id : null;
    }

    public void setActiveContext(String contextID) {
        activeContext = contexts.get(contextID);
    }

    void registerContext(InputContext context) {
        contexts.put(context.id, context);
    }

    private void init() throws Exception {

        Element root = Util.parseXML("keybindings");

        NodeList contextsList = root.getElementsByTagName("context");
        for (int i = 0; i < contextsList.getLength(); i++) {

            Element contextNode = (Element) contextsList.item(i);
            if (!contextNode.getParentNode().equals(root))
                continue;

            InputContext.parseXML(this, contextNode, null);
        }
    }
}
