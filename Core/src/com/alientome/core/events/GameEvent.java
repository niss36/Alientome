package com.alientome.core.events;

/**
 * A GameEvent is a type of Event which is created and dispatched within GameEventDispatcher systems.
 * Its role is to send a message according to its GameEventType. Subclasses may provide additional data relative to the event.
 *
 * @see GameEventDispatcher
 * @see GameEventType
 */
public class GameEvent {

    /**
     * The type of this GameEvent.
     */
    public final GameEventType type;

    public GameEvent(GameEventType type) {
        this.type = type;
    }
}
