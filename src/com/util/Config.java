package com.util;

import com.util.listeners.ConfigListener;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.io.*;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;
import java.util.function.Consumer;

import static com.util.Util.closeSilently;
import static com.util.Util.log;

public final class Config {
    private static final Config ourInstance = new Config();
    private final String[] keyNames = {"Key.Jump", "Key.MoveLeft", "Key.MoveRight", "Key.Fire", "Key.Debug", "Key.Pause"};
    private final String[] optionNames = {"UI.PauseOnLostFocus", "Debug.ShowBlockIn", "Debug.ShowSightLines"};
    private final File userConfig;
    private final ArrayList<ConfigListener> listeners = new ArrayList<>();
    private final Properties defaultProperties;
    private final HashMap<String, Object> values = new HashMap<>();
    private Properties properties;

    private Config() {
        userConfig = FileManager.getInstance().getConfig();

        defaultProperties = getProperties(defaultConfig());

        enumerate(defaultProperties.propertyNames(), defaultProperties);
    }

    public static Config getInstance() {
        return ourInstance;
    }

    public void load() {

        log("Loading config", 0);

        InputStream stream;

        try {
            stream = new FileInputStream(userConfig);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            stream = null;
        }

        properties = getProperties(stream);

        if (properties == null) {
            log("Using default Config", 1);
            return;
        }

        String gameVersion = getString("Version");
        String configVersion = properties.getProperty("Version");

        if (!gameVersion.equals(configVersion)) {
            log("Config version mismatch. Game version : " + gameVersion + ". Config version : " + configVersion, 1);

            properties.setProperty("Version", gameVersion);
        }

        Enumeration<?> defaultNames = defaultProperties.propertyNames();
        while (defaultNames.hasMoreElements()) {

            String name = (String) defaultNames.nextElement();

            if (properties.getProperty(name) == null) {
                log("Missing property '" + name + "', creating with default value...", 1);
                properties.setProperty(name, defaultProperties.getProperty(name));
            }
        }

        Enumeration<?> configNames = properties.propertyNames();
        while (configNames.hasMoreElements()) {

            String name = (String) configNames.nextElement();

            if (!defaultProperties.containsKey(name)) {
                log("Unknown property '" + name + "', ignoring...", 1);
            }
        }

        enumerate(defaultProperties.propertyNames(), properties);

        log("Loaded config", 0);
    }

    public void save() {

        log("Saving config", 0);
        try (FileOutputStream fos = new FileOutputStream(userConfig)) {
            properties.store(fos, null);
            log("Saved config", 0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private InputStream defaultConfig() {
        return getClass().getResourceAsStream("DefaultConfig.properties");
    }

    public void createConfigFile(File file) {

        assert !file.exists();

        try (InputStream in = defaultConfig()) {
            Files.copy(in, file.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void enumerate(Enumeration<?> propertyNames, Properties properties) {

        boolean def = properties == defaultProperties;

        while (propertyNames.hasMoreElements()) {

            String name = (String) propertyNames.nextElement();

            Object obj;
            String property = properties.getProperty(name);

            if (name.startsWith("Key.")) {
                obj = keyValue(property, def ? -1 : getInt(name));
            } else if (property.equalsIgnoreCase("true")) obj = true;
            else if (property.equalsIgnoreCase("false")) obj = false;
            else obj = property;

            values.put(name, obj);
        }
    }

    public void addConfigListener(ConfigListener listener) {
        listeners.add(listener);
    }

    public void reset() {
        properties = getProperties(defaultConfig());

        enumerate(defaultProperties.propertyNames(), properties);

        notifyListeners(ConfigListener::configReset);
    }

    public void resetKeys() {

        for (int i = 0; i < keyNames.length; i++) {

            String name = defaultProperties.getProperty(keyNames[i]);

            setKey(i, name, keyValue(name, -1));
        }

        notifyListeners(ConfigListener::configKeysReset);
    }

    private void notifyListeners(Consumer<? super ConfigListener> action) {

        SwingUtilities.invokeLater(() -> listeners.forEach(action));
    }

    private Properties getProperties(InputStream stream) {

        if (stream == null) return null;

        Properties properties = new Properties();

        try {
            properties.load(stream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeSilently(stream);
        }

        return properties;
    }

    private Integer keyValue(String name, int defaultValue) {

        name = "VK_" + name;

        for (Field f : KeyEvent.class.getFields()) {

            if (f.getName().equals(name)) {

                try {
                    return (Integer) f.get(null);

                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        log("Key name " + name + " is incorrect. Using default value.", 1);

        return defaultValue;
    }

    public Object get(String key) {

        return values.get(key);
    }

    public String getString(String key) {

        return (String) get(key);
    }

    public boolean getBoolean(String key) {

        return (boolean) get(key);
    }

    public int getInt(String key) {

        return (int) get(key);
    }

    public int getKey(int index) {

        return getInt(keyNames[index]);
    }

    public void setKey(int index, String keyName, int keyCode) {

        properties.setProperty(keyNames[index], keyName);
        values.put(keyNames[index], keyCode);
    }

    public boolean isKeyUsed(int keyCode) {
        for (String keyName : keyNames) if (getInt(keyName) == keyCode) return true;

        return false;
    }

    public boolean getOption(int index) {

        return getBoolean(optionNames[index]);
    }

    public void setOption(int index, boolean value) {

        properties.setProperty(optionNames[index], "" + value);
        values.put(optionNames[index], value);
    }
}
