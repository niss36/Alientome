package com.alientome.game.events;

import com.alientome.core.events.GameEvent;
import com.alientome.core.events.GameEventType;

public class GameResumeEvent extends GameEvent {

    public GameResumeEvent() {
        super(GameEventType.GAME_RESUME);
    }
}
