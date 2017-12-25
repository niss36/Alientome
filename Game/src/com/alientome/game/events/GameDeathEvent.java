package com.alientome.game.events;

import com.alientome.core.events.GameEvent;
import com.alientome.core.events.GameEventType;

public class GameDeathEvent extends GameEvent {

    public GameDeathEvent() {
        super(GameEventType.GAME_DEATH);
    }
}
