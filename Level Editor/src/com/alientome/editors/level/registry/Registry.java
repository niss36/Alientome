package com.alientome.editors.level.registry;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class Registry<T> {

    private final Map<String, T> reg = new LinkedHashMap<>();

    public void set(String key, T value) {
        reg.put(key, value);
    }

    public T get(String key) {
        return reg.get(key);
    }

    public Collection<T> values() {
        return reg.values();
    }
}
