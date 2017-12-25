package com.alientome.game.level;

public interface LevelManager {

    void reset();

    void nextLevel();

    void loadLevel(String path);

    void dispose();

    Level getLevel();
}
