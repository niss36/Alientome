package com.alientome.game.entities.parse;

import com.alientome.game.util.EntityTags;

public class EntityState {

    public final String identifier;
    public final int spawnX;
    public final int spawnY;
    public final EntityTags tags;

    public EntityState(String identifier, int spawnX, int spawnY, EntityTags tags) {
        this.identifier = identifier;
        this.spawnX = spawnX;
        this.spawnY = spawnY;
        this.tags = tags;
    }
}
