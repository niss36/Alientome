package com.alientome.core;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;

import java.util.HashMap;
import java.util.Map;

public class SharedInstances {

    private static final Map<String, Property<?>> instances = new HashMap<>();

    @SuppressWarnings("unchecked")
    public static <T> Property<T> getProperty(String id) {
        return (Property<T>) instances.computeIfAbsent(id, s -> new SimpleObjectProperty<T>());
    }

    public static void set(String id, Object instance) {
        getProperty(id).setValue(instance);
    }

    public static <T> T get(String id) {
        return SharedInstances.<T>getProperty(id).getValue();
    }

    public static void remove(String id) {
        instances.remove(id);
    }

    public static void clear() {
        instances.clear();
    }
}
