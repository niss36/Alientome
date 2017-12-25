package com.alientome.core.events;

/**
 * GameEventTypes identify GameEvents by providing an Event type, which can be used to bind and listen to specific types
 * of events.
 *
 * @see GameEvent
 * @see GameEventListener
 * @see GameEventDispatcher
 */
public enum GameEventType {

    /**
     * Will start a new Game.
     */
    GAME_START,

    /**
     * Will exit current Game.
     */
    GAME_EXIT,

    /**
     * Will pause current Game.
     */
    GAME_PAUSE,

    /**
     * Will resume current Game.
     */
    GAME_RESUME,

    /**
     * Will reset current Game's Level.
     */
    GAME_RESET,

    /**
     * Fired when the user-controlled Entity dies. Triggers death menu.
     */
    GAME_DEATH,

    /**
     * Fired when an uncaught exception occurs in Game loop. Triggers an error message and exits the game.
     */
    GAME_ERROR,

    /**
     * Triggers the reset of the current Config.
     */
    CONFIG_RESET,

    /**
     * Triggers the reset of the current Keybindings.
     */
    KEYBINDINGS_RESET,

    /**
     *
     */
    MESSAGE_SENT,

    /**
     *
     */
    QUIT_REQUEST,

    /**
     *
     */
    QUIT
}
