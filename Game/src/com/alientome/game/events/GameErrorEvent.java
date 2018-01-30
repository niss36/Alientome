package com.alientome.game.events;

import com.alientome.core.events.GameEvent;
import com.alientome.core.events.GameEventType;

public class GameErrorEvent extends GameEvent {

    public final Throwable error;

    public GameErrorEvent(Throwable error) {

        super(GameEventType.GAME_ERROR);

        this.error = error;
    }
}
