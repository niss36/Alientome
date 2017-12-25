package com.alientome.core.events;

/**
 * A GameEventListener is a listener who accepts GameEvents and reacts according to them.
 *
 * @see GameEvent
 * @see GameEventDispatcher
 */
@FunctionalInterface
public interface GameEventListener {

    /**
     * Takes any action. In normal circumstances, this method will be called within the dispatch Thread.
     *
     * @param e the received event
     */
    void executeEvent(GameEvent e);
}
