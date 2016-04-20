package com.util;

import java.awt.event.KeyEvent;
import java.io.*;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Properties;

public class Config {
    private static final Config ourInstance = new Config();
    private final HashMap<String, Integer> keys = new HashMap<>();
    private final String[] names = {"Key.Jump", "Key.MoveLeft", "Key.MoveRight", "Key.Fire", "Key.Debug", "Key.Pause"};
    private final int[] defaults = new int[names.length];
    private final File userConfig = new File(FileNames.config);
    private Properties properties;

    private Config() {
        Properties defaultProperties = getProperties(defaultConfig());

        for (int i = 0; i < names.length; i++) {
            defaults[i] = keyValue(defaultProperties.getProperty(names[i]), 0);
            keys.put(names[i], defaults[i]);
        }
    }

    public static Config getInstance() {
        return ourInstance;
    }

    public void load() {

        checkFiles();

        InputStream stream;

        try {
            stream = new FileInputStream(userConfig);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            stream = null;
        }

        properties = getProperties(stream);

        if (properties == null) {
            System.out.println("Using default Config");
            return;
        }

        for (int i = 0; i < names.length; i++) {

            int v = keyValue(properties.getProperty(names[i]), defaults[i]);

            keys.put(names[i], v);
        }
    }

    public void save() {

        try {
            properties.store(new FileOutputStream(userConfig), null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private InputStream defaultConfig() {
        return getClass().getResourceAsStream("DefaultConfig.properties");
    }

    private void checkFiles() {

        File dir = new File(FileNames.directory);
        if (!dir.exists() && !dir.mkdir()) System.out.println("Base directory could not be created.");

        if (!userConfig.exists()) try {
            InputStream in = defaultConfig();
            Files.copy(in, userConfig.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Properties getProperties(InputStream stream) {

        if (stream == null) return null;

        Properties properties = new Properties();

        try {
            properties.load(stream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return properties;
    }

    private int keyValue(String name, int defaultValue) {

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

        System.out.println("Key name " + name + " is incorrect. Using default value.");

        return defaultValue;
    }

    public int getKey(String name) {
        return keys.get(name);
    }

    public int getKey(int index) {
        return getKey(names[index]);
    }

    public void updatePropertiesAndKeyMap(int index, String keyName, int keyCode) {
        properties.setProperty(names[index], keyName);
        keys.put(names[index], keyCode);
    }

    public boolean isUsed(int keyCode) {
        return keys.containsValue(keyCode);
    }
}
