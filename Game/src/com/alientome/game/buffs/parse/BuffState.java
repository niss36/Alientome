package com.alientome.game.buffs.parse;

public class BuffState {

    public final String identifier;
    public final int spawnX;
    public final int spawnY;
    public final Object[] args;

    public BuffState(String identifier, int spawnX, int spawnY, Object... args) {
        this.identifier = identifier;
        this.spawnX = spawnX;
        this.spawnY = spawnY;
        this.args = args;
    }
}
