package com.alientome.editors.level.state;

import com.alientome.editors.level.background.Layer;

import java.util.List;

public class LevelState {

    public final String name;
    public final BlockState[][] tiles;
    public final List<Entity> entityList;
    public final List<ScriptObject> scripts;
    public final List<Layer> layers;
    public final int bgScale;
    public final int playerX;
    public final int playerY;

    public LevelState(String name, BlockState[][] tiles, List<Entity> entityList, List<ScriptObject> scripts, List<Layer> layers, int bgScale, int playerX, int playerY) {
        this.name = name;
        this.tiles = tiles;
        this.entityList = entityList;
        this.scripts = scripts;
        this.layers = layers;
        this.bgScale = bgScale;
        this.playerX = playerX;
        this.playerY = playerY;
    }
}
