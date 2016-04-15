package com.util;

import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Properties;

public class Config {
    private static final Config ourInstance = new Config();
    private final HashMap<String, Integer> keys = new HashMap<>();
    private final String[] names = {"Key.Jump", "Key.MoveLeft", "Key.MoveRight", "Key.Fire", "Key.Debug", "Key.Pause"};
    private final int[] defaults = {KeyEvent.VK_SPACE, KeyEvent.VK_A, KeyEvent.VK_D, KeyEvent.VK_X, KeyEvent.VK_H, KeyEvent.VK_ESCAPE};
    private Properties properties;

    private Config() {
        for (int i = 0; i < names.length; i++) {
            keys.put(names[i], defaults[i]);
        }
    }

    public static Config getInstance() {
        return ourInstance;
    }

    public void load() {

        properties = getProperties();

        if (properties == null) {
            System.out.println("Using default Config");
            return;
        }

        for (int i = 0; i < names.length; i++) {

            int v = keyValue(properties.getProperty(names[i]), defaults[i]);

            keys.put(names[i], v);
        }
    }

    private Properties getProperties() {

        InputStream stream = getClass().getResourceAsStream("DefaultConfig.properties");

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
