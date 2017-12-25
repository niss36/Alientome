package com.alientome.game.events;

import com.alientome.core.events.GameEvent;
import com.alientome.core.events.GameEventType;

public class GamePauseEvent extends GameEvent {

    public GamePauseEvent() {
        super(GameEventType.GAME_PAUSE);
    }
}
