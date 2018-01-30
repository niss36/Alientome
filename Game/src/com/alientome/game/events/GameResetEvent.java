package com.alientome.game.events;

import com.alientome.core.events.GameEvent;
import com.alientome.core.events.GameEventType;

public class GameResetEvent extends GameEvent {

    public GameResetEvent() {
        super(GameEventType.GAME_RESET);
    }
}
