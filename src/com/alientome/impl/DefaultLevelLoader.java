package com.alientome.impl;

import com.alientome.game.level.LevelLoader;
import com.alientome.game.level.LevelManager;
import com.alientome.impl.level.CompoundLevelManager;
import com.alientome.impl.level.SaveLevelManager;

import java.io.File;
import java.io.IOException;

public class DefaultLevelLoader implements LevelLoader {

    @Override
    public LevelManager loadFrom(File temp, File actual) throws IOException {
        return new CompoundLevelManager(temp, actual);
    }

    @Override
    public LevelManager loadFrom(File file) throws IOException {
        return new CompoundLevelManager(file);
    }

    @Override
    public LevelManager loadFrom(int saveIndex) throws IOException {
        return new SaveLevelManager(saveIndex);
    }
}
