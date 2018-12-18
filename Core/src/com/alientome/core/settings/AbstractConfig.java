package com.alientome.core.settings;

import com.alientome.core.Context;
import com.alientome.core.util.Logger;
import com.alientome.core.util.VersionConflictData;
import com.alientome.core.util.WrappedXML;
import javafx.application.Platform;
import javafx.beans.property.*;

import java.io.*;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.alientome.core.events.GameEventType.CONFIG_RESET;

public abstract class AbstractConfig implements Config {

    protected static final Logger log = Logger.get();

    protected final Context context;
    protected final Map<String, Setting> settings = new LinkedHashMap<>();
    protected final Map<String, Property<?>> properties = new HashMap<>();

    private boolean needsSave;

    public AbstractConfig(Context context) {
        this.context = context;
    }

    @Override
    public VersionConflictData load() {

        context.getDispatcher().register(CONFIG_RESET, e -> Platform.runLater(this::reset));

        log.i("Loading config");

        try {
            init();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Exception while loading settings", e);
        }

        read(defaultConfig());

        String gameVersion = this.<String>getProperty("version").getValue();

        read(userConfig());

        String userVersion = this.<String>getProperty("version").getValue();

        needsSave = false;

        if (gameVersion.equals(userVersion))
            return null;

        return new VersionConflictData(gameVersion, userVersion);
    }

    protected void init() throws IOException, ClassNotFoundException {

        WrappedXML xml = getXML();

        for (WrappedXML settingXML : xml.nodesWrapped("settings/setting")) {
            Setting setting = Setting.parseXML(settingXML);
            settings.put(setting.id, setting);
        }
    }

    private void read(InputStream stream) {

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
            List<String> lines = reader.lines().collect(Collectors.toList());
            read(lines);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void read(File file) {

        try {
            List<String> lines = Files.readAllLines(file.toPath());
            read(lines);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void read(List<String> lines) {

        for (String line : lines) {

            String[] keyValPair = line.split("=");
            String key = keyValPair[0];
            String val = keyValPair[1];

            Setting setting = settings.get(key);
            if (setting != null) {
                Property<?> property = parse(setting.valueType, key, val);
                property.addListener(observable -> needsSave = true);
                properties.put(key, property);
            } else log.w("Unknown key found : '" + key + "'. Discarding.");
        }
    }

    @Override
    public boolean needsSave() {
        return needsSave;
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

            needsSave = false;
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
    public <T> Property<T> getProperty(String key) {
        // Inherently unsafe as we are dealing with data that's not compile-time checked.
        // This is purely for convenience, which is fine if the caller knows the setting's runtime type.
        return (Property<T>) properties.get(key);
    }

    @Override
    public IntegerProperty getIntegerProperty(String key) {
        return (IntegerProperty) properties.get(key);
    }

    @Override
    public boolean getAsBoolean(String key) {
        return this.<Boolean>getProperty(key).getValue();
    }

    @Override
    public int getAsInt(String key) {
        return this.<Integer>getProperty(key).getValue();
    }

    private final Map<Class, PropertyProvider> propertyProviders = new HashMap<>();
    
    protected final <T> void addPropertyProvider(Class<? extends T> type, PropertyProvider<T> provider) {
        propertyProviders.put(type, provider);
    }

    protected final <T> PropertyProvider<T> getPropertyProvider(Class<? extends T> type) {
        // Not strictly safe, but will do what's expected of it.
        return propertyProviders.get(type);
    }

    {
        addPropertyProvider(String.class, (current, s) -> {
            if (current != null) {
                current.setValue(s);
                return current;
            }
            return new SimpleStringProperty(s);
        });
        this.<Number>addPropertyProvider(Integer.class, (current, s) -> {
            if (current != null) {
                current.setValue(Integer.valueOf(s));
                return current;
            }
            return new SimpleIntegerProperty(Integer.parseInt(s));
        });
        this.<Number>addPropertyProvider(Double.class, (current, s) -> {
            if (current != null) {
                current.setValue(Double.valueOf(s));
                return current;
            }
            return new SimpleDoubleProperty(Double.parseDouble(s));
        });
        addPropertyProvider(Boolean.class, (current, s) -> {
            if (current != null) {
                current.setValue(Boolean.valueOf(s));
                return current;
            }
            return new SimpleBooleanProperty(Boolean.parseBoolean(s));
        });
    }

    private <T> Property<T> parse(Class<? extends T> type, String key, String s) {

        Property<T> current = getProperty(key);

        PropertyProvider<T> provider = getPropertyProvider(type);

        return provider.parse(current, s);
    }
}
