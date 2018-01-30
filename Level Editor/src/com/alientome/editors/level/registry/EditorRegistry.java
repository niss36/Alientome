package com.alientome.editors.level.registry;

import com.alientome.editors.level.state.BlockState;
import com.alientome.editors.level.state.EntityState;

public class EditorRegistry {

    private final Registry<BlockState> blocksRegistry = new Registry<>();
    private final Registry<EntityState> entitiesRegistry = new Registry<>();

    public Registry<BlockState> getBlocksRegistry() {
        return blocksRegistry;
    }

    public Registry<EntityState> getEntitiesRegistry() {
        return entitiesRegistry;
    }
}
