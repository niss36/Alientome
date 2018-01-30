package com.alientome.core.settings;

import com.alientome.core.util.VersionConflictData;
import javafx.beans.property.Property;

import java.io.File;

public interface Config {

    VersionConflictData load();

    void resolveConflict(VersionConflictData data);

    void save();

    void reset();

    void createConfigFile(File target);

    <T> Property<T> getProperty(String key);

    boolean getAsBoolean(String key);

    int getAsInt(String key);
}
