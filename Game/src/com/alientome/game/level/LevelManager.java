package com.alientome.game.level;

import com.alientome.game.GameContext;

public interface LevelManager {

    void reset();

    void nextLevel();

    void loadLevel(String path);

    void dispose();

    Level getLevel();

    GameContext getContext();
}
