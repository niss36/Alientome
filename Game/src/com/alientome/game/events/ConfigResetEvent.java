package com.alientome.game.events;

import com.alientome.core.events.GameEvent;
import com.alientome.core.events.GameEventType;

public class ConfigResetEvent extends GameEvent {

    public ConfigResetEvent() {
        super(GameEventType.CONFIG_RESET);
    }
}
