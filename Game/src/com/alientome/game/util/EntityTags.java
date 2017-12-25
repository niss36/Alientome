package com.alientome.game.util;

import java.util.Map;
import java.util.function.Function;

public class EntityTags {

    private final Map<String, String> tags;

    public EntityTags(Map<String, String> tags) {
        this.tags = tags;
    }

    public String get(String key, String defaultVal) {
        return tags.getOrDefault(key, defaultVal);
    }

    public <T> T getAs(String key, T defaultVal, Function<String, T> converter) {
        String val = tags.get(key);
        return val == null ? defaultVal : converter.apply(val);
    }

    public int getAsInt(String key, int defaultVal) {
        String val = tags.get(key);
        return val == null ? defaultVal : Integer.parseInt(val);
    }

    public float getAsFloat(String key, float defaultVal) {
        String val = tags.get(key);
        return val == null ? defaultVal : Float.parseFloat(val);
    }

    public double getAsDouble(String key, double defaultVal) {
        String val = tags.get(key);
        return val == null ? defaultVal : Double.parseDouble(val);
    }
}
