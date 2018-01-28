package com.alientome.core.settings;

import com.alientome.core.Context;
import com.alientome.core.util.Logger;
import com.alientome.core.util.WrappedXML;
import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

import java.io.*;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import static com.alientome.core.events.GameEventType.CONFIG_RESET;

public abstract class AbstractConfig implements Config {

    protected static final Logger log = Logger.get();

    protected final Context context;
    protected final Map<Class<?>, BiFunction<Property<?>, String, Property<?>>> propertyProviders = new HashMap<>();
    protected final Map<String, Setting> settings = new LinkedHashMap<>();
    protected final Map<String, Property<?>> properties = new HashMap<>();

    public AbstractConfig(Context context) {
        this.context = context;
    }

    @Override
    public void load() {

        context.getDispatcher().register(CONFIG_RESET, e -> Platform.runLater(this::reset));

        log.i("Loading config");

        try {
            init();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Exception while loading settings", e);
        }

        read(defaultConfig());

        read(userConfig());
    }

    protected void init() throws IOException, ClassNotFoundException {

        WrappedXML xml = getXML();

        for (WrappedXML settingXML : xml.nodesWrapped("settings/setting")) {
            Setting setting = Setting.parseXML(settingXML);
            settings.put(setting.id, setting);
        }
    }

    protected void read(InputStream stream) {

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {

            List<String> lines = reader.lines().collect(Collectors.toList());
            read(lines);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void read(File file) {

        try {
            List<String> lines = Files.readAllLines(file.toPath());
            read(lines);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void read(List<String> lines) {

        for (String line : lines) {

            String[] keyValPair = line.split("=");
            String key = keyValPair[0];
            String val = keyValPair[1];

            Setting setting = settings.get(key);
            if (setting != null) {
                Property<?> current = properties.get(key);
                Property<?> property = parse(setting.valueType, current, val);
                properties.put(key, property);
            } else log.w("Unknown key found : '" + key + "'. Discarding.");
        }
    }

    @Override
    public void save() {

        log.i("Saving config");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(userConfig()))) {

            for (Map.Entry<String, Property<?>> entry : properties.entrySet()) {

                String line = entry.getKey() + "=" + entry.getValue().getValue();

                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            log.e("Could not save config :");
            e.printStackTrace();
        }
    }

    @Override
    public void reset() {
        read(defaultConfig());
    }

    @Override
    public void createConfigFile(File targetFile) {

        try (InputStream in = defaultConfig()) {
            Files.copy(in, targetFile.toPath());
            log.i("Config file was created");
        } catch (IOException e) {
            e.printStackTrace();
            log.w("Config file could not be created");
        }
    }

    protected abstract InputStream defaultConfig();

    protected abstract File userConfig();

    protected abstract WrappedXML getXML() throws IOException;

    @Override
    @SuppressWarnings("unchecked")
    public <T> Property<T> getProperty(String key) {
        return (Property<T>) properties.get(key);
    }

    @Override
    public boolean getAsBoolean(String key) {
        return this.<Boolean>getProperty(key).getValue();
    }

    @Override
    public int getAsInt(String key) {
        return this.<Integer>getProperty(key).getValue();
    }

    {
        propertyProviders.put(String.class, (p, s) -> {
            if (p != null) return forceSet(p, s);
            return new SimpleStringProperty(s);
        });
        propertyProviders.put(Integer.class, (p, s) -> {
            if (p != null) return forceSet(p, Integer.valueOf(s));
            return new SimpleIntegerProperty(Integer.parseInt(s));
        });
        propertyProviders.put(Boolean.class, (p, s) -> {
            if (p != null) return forceSet(p, Boolean.valueOf(s));
            return new SimpleBooleanProperty(Boolean.parseBoolean(s));
        });
    }

    @SuppressWarnings("unchecked")
    private <T> Property<?> forceSet(Property<?> source, T value) {
        ((Property<T>) source).setValue(value);
        return source;
    }

    private Property<?> parse(Class<?> valueType, Property<?> current, String valueString) {

        return propertyProviders.get(valueType).apply(current, valueString);
    }
}
