package com.alientome.core.settings;

import javafx.beans.property.Property;

import java.io.File;

public interface Config {

    void load();

    void save();

    void reset();

    void createConfigFile(File target);

    <T> Property<T> getProperty(String key);

    boolean getAsBoolean(String key);

    int getAsInt(String key);
}
