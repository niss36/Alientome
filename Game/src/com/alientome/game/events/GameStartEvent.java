package com.alientome.game.events;

import com.alientome.core.events.GameEvent;
import com.alientome.core.events.GameEventType;
import com.alientome.game.level.LevelManager;

public class GameStartEvent extends GameEvent {

    public final LevelManager manager;

    public GameStartEvent(LevelManager manager) {

        super(GameEventType.GAME_START);

        this.manager = manager;
    }
}
