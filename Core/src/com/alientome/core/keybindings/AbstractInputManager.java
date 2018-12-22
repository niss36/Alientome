package com.alientome.core.keybindings;

import com.alientome.core.Context;
import com.alientome.core.events.KeybindingsResetEvent;
import com.alientome.core.util.Logger;
import com.alientome.core.util.WrappedXML;
import javafx.beans.property.Property;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public abstract class AbstractInputManager implements InputManager {

    protected static final Logger log = Logger.get();

    protected final Context context;
    protected final Map<String, InputContext> contexts = new LinkedHashMap<>();
    protected InputContext activeContext;

    private boolean needsSave;

    protected AbstractInputManager(Context context) {
        this.context = context;
    }

    @Override
    public void load() throws IOException {

        log.i("Loading keybindings");
        init();

        read(defaultKeybindings());

        read(userKeybindings());

        needsSave = false;
    }

    private void init() throws IOException {

        WrappedXML xml = getXML();

        for (WrappedXML contextXML : xml.nodesWrapped("keybindings/context"))
            InputContext.parseXML(this, contextXML, null);
    }

    private void read(InputStream stream) throws IOException {

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
            List<String> lines = reader.lines().collect(Collectors.toList());
            read(lines);
        }
    }

    private void read(Path path) throws IOException {

        List<String> lines = Files.readAllLines(path);
        read(lines);
    }

    private void read(List<String> lines) {
        for (InputContext context : contexts.values())
            context.read(lines, observable -> needsSave = true);
    }

    @Override
    public boolean needsSave() {
        return needsSave;
    }

    @Override
    public void save() throws IOException {

        log.i("Saving keybindings");
        try (BufferedWriter writer = Files.newBufferedWriter(userKeybindings())) {

            SortedMap<String, String> values = new TreeMap<>();

            for (InputContext context : contexts.values()) context.save(values);

            for (Map.Entry<String, String> entry : values.entrySet()) {

                String line = entry.getKey() + "=" + entry.getValue();

                writer.write(line);
                writer.newLine();
            }

            needsSave = false;
        }
    }

    @Override
    public void reset() throws IOException {
        read(defaultKeybindings());
        context.getDispatcher().submit(new KeybindingsResetEvent());
    }

    @Override
    public void createDefaultFile(Path target) throws IOException {

        try (InputStream stream = defaultKeybindings()) {
            Files.copy(stream, target);
        }
    }

    protected abstract InputStream defaultKeybindings();

    protected abstract Path userKeybindings();

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
