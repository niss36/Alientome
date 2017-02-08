package com.events;

public class GameEvent {

    public final Object source;
    public final GameEventType type;

    public GameEvent(Object source, GameEventType type) {
        this.source = source;
        this.type = type;
    }
}
