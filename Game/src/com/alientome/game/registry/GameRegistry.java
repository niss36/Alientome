package com.alientome.game.registry;

import com.alientome.game.blocks.Block;
import com.alientome.game.buffs.Buff;
import com.alientome.game.commands.Command;
import com.alientome.game.entities.Entity;

import java.util.ArrayList;
import java.util.List;

public class GameRegistry {

    private final Registry<Class<? extends Block>> blocksRegistry = new Registry<>();
    private final Registry<Class<? extends Entity>> entitiesRegistry = new Registry<>();
    private final Registry<Class<? extends Buff>> buffsRegistry = new Registry<>();
    private final List<Command> commandsRegistry = new ArrayList<>();

    public Registry<Class<? extends Block>> getBlocksRegistry() {
        return blocksRegistry;
    }

    public Registry<Class<? extends Entity>> getEntitiesRegistry() {
        return entitiesRegistry;
    }

    public Registry<Class<? extends Buff>> getBuffsRegistry() {
        return buffsRegistry;
    }

    public List<Command> getCommandsRegistry() {
        return commandsRegistry;
    }
}
