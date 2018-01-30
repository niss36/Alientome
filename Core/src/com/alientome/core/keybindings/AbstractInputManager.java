package com.alientome.core.keybindings;

import com.alientome.core.Context;
import com.alientome.core.util.Logger;
import com.alientome.core.util.WrappedXML;
import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

import static com.alientome.core.events.GameEventType.KEYBINDINGS_RESET;

public abstract class AbstractInputManager implements InputManager {

    protected static final Logger log = Logger.get();

    protected final Context context;
    protected final Map<String, InputContext> contexts = new LinkedHashMap<>();
    protected InputContext activeContext;

    protected AbstractInputManager(Context context) {
        this.context = context;
    }

    @Override
    public void load() {

        context.getDispatcher().register(KEYBINDINGS_RESET, e -> Platform.runLater(this::reset));

        log.i("Loading keybindings");
        try {
            init();
        } catch (Exception e) {
            log.f("Uncaught exception while loading key bindings :");
            throw new RuntimeException(e);
        }

        read(defaultKeybindings());

        read(userKeybindings());
    }

    protected void init() throws IOException {

        WrappedXML xml = getXML();

        for (WrappedXML contextXML : xml.nodesWrapped("keybindings/context"))
            InputContext.parseXML(this, contextXML, null);
    }

    protected void read(InputStream stream) {

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
            List<String> lines = reader.lines().collect(Collectors.toList());

            for (InputContext context : contexts.values()) context.read(lines);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void read(File file) {

        try {
            List<String> lines = Files.readAllLines(file.toPath());

            for (InputContext context : contexts.values()) context.read(lines);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void save() {

        log.i("Saving keybindings");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(userKeybindings()))) {

            SortedMap<String, String> values = new TreeMap<>();

            for (InputContext context : contexts.values()) context.save(values);

            for (Map.Entry<String, String> entry : values.entrySet()) {

                String line = entry.getKey() + "=" + entry.getValue();

                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
            log.e("Could not save keybindings");
        }
    }

    @Override
    public void reset() {
        read(defaultKeybindings());
    }

    @Override
    public void createKeybindingsFile(File targetFile) {

        try (InputStream stream = defaultKeybindings()) {
            Files.copy(stream, targetFile.toPath());
            log.i("Keybindings file was created");
        } catch (IOException e) {
            e.printStackTrace();
            log.w("Keybindings file could not be created");
        }
    }

    protected abstract InputStream defaultKeybindings();

    protected abstract File userKeybindings();

    protected abstract WrappedXML getXML() throws IOException;

    @Override
    public void setListener(String contextID, String bindingID, InputListener listener) {
        contexts.get(contextID).setListener(bindingID, listener);
    }

    @Override
    public Property<KeyCode> bindingProperty(String contextID, String bindingID) {
        return contexts.get(contextID).bindingProperty(bindingID);
    }

    @Override
    public boolean isBound(String contextID, KeyCode code) {
        return contexts.get(contextID).isBound(code);
    }

    @Override
    public boolean consumeEvent(KeyEvent e) {
        return activeContext != null && activeContext.consumeEvent(e);
    }

    public String getActiveContext() {
        return activeContext == null ? null : activeContext.id;
    }

    @Override
    public void setActiveContext(String contextID) {
        activeContext = contexts.get(contextID);
    }

    protected void registerContext(InputContext context) {
        contexts.put(context.id, context);
    }
}
