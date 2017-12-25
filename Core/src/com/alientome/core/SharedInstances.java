package com.alientome.core;

import java.util.HashMap;
import java.util.Map;

public class SharedInstances {

    private static final Map<String, Object> instances = new HashMap<>();

    public static void set(String id, Object instance) {
        instances.put(id, instance);
    }

    @SuppressWarnings("unchecked")
    public static <T> T get(String id) {
        return (T) instances.get(id);
    }

    public static void remove(String id) {
        instances.remove(id);
    }

    public static void clear() {
        instances.clear();
    }
}
