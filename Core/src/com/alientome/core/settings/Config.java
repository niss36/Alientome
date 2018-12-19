package com.alientome.core.settings;

import com.alientome.core.util.VersionConflictData;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.Property;

import java.io.IOException;
import java.nio.file.Path;

public interface Config {

    VersionConflictData load() throws IOException;

    void resolveConflict(VersionConflictData data);

    boolean needsSave();

    void save();

    void reset() throws IOException;

    void createDefaultFile(Path target) throws IOException;

    <T> Property<T> getProperty(String key);

    IntegerProperty getIntegerProperty(String key);

    boolean getAsBoolean(String key);

    int getAsInt(String key);
}
