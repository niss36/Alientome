package com.alientome.game.registry;

import java.util.HashMap;
import java.util.Map;

public class Registry<T> {

    private final Map<String, T> reg = new HashMap<>();

    public void set(String key, T value, boolean override) {

        if (reg.containsKey(key) && !override)
            throw new IllegalStateException("Object " + key + " was already registered (Override : false)");
        if (!reg.containsKey(key) && override)
            throw new IllegalStateException("Object " + key + " was not already registered (Override : true)");
        reg.put(key, value);
    }

    public void set(String key, T value) {
        set(key, value, false);
    }

    public T get(String key) {
        return reg.get(key);
    }
}
