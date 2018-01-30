package com.alientome.core.events;

public class QuitRequestEvent extends GameEvent {

    private boolean cancelled = false;

    public QuitRequestEvent() {
        super(GameEventType.QUIT_REQUEST);
    }

    public void cancel() {
        cancelled = true;
    }

    public boolean isCancelled() {
        return cancelled;
    }
}
