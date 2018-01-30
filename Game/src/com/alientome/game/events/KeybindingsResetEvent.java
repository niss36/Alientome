package com.alientome.game.events;

import com.alientome.core.events.GameEvent;
import com.alientome.core.events.GameEventType;

public class KeybindingsResetEvent extends GameEvent {

    public KeybindingsResetEvent() {
        super(GameEventType.KEYBINDINGS_RESET);
    }
}
