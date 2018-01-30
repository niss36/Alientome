package com.alientome.game.events;

import com.alientome.core.events.GameEvent;
import com.alientome.core.events.GameEventType;
import com.alientome.game.commands.messages.ConsoleMessage;

public class MessageEvent extends GameEvent {

    public final ConsoleMessage message;

    public MessageEvent(ConsoleMessage message) {
        super(GameEventType.MESSAGE_SENT);

        this.message = message;
    }
}
