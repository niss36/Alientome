package com.alientome.editors.level.state;

import java.util.List;

public class LevelState {

    public final String name;
    public final BlockState[][] tiles;
    public final List<Entity> entityList;
    public final List<ScriptObject> scripts;
    public final int playerX;
    public final int playerY;

    public LevelState(String name, BlockState[][] tiles, List<Entity> entityList, List<ScriptObject> scripts, int playerX, int playerY) {
        this.name = name;
        this.tiles = tiles;
        this.entityList = entityList;
        this.scripts = scripts;
        this.playerX = playerX;
        this.playerY = playerY;
    }
}
