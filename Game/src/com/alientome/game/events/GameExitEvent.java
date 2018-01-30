package com.alientome.game.events;

import com.alientome.core.events.GameEvent;
import com.alientome.core.events.GameEventType;

public class GameExitEvent extends GameEvent {

    public GameExitEvent() {
        super(GameEventType.GAME_EXIT);
    }
}
