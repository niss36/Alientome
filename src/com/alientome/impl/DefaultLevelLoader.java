package com.alientome.impl;

import com.alientome.game.GameContext;
import com.alientome.game.level.LevelLoader;
import com.alientome.game.level.LevelManager;
import com.alientome.impl.level.CompoundLevelManager;
import com.alientome.impl.level.SaveLevelManager;

import java.io.IOException;
import java.nio.file.Path;

public class DefaultLevelLoader implements LevelLoader {

    private final GameContext context;

    public DefaultLevelLoader(GameContext context) {
        this.context = context;
    }

    @Override
    public LevelManager loadFrom(Path temp, Path actual) throws IOException {
        return new CompoundLevelManager(context, temp, actual);
    }

    @Override
    public LevelManager loadFrom(Path file) throws IOException {
        return new CompoundLevelManager(context, file);
    }

    @Override
    public LevelManager loadFrom(int saveIndex) throws IOException {
        return new SaveLevelManager(context, saveIndex);
    }
}
