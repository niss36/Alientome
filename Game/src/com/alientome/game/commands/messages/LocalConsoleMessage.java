package com.alientome.game.commands.messages;

import com.alientome.core.internationalization.I18N;

public class LocalConsoleMessage implements ConsoleMessage {

    private final String key;
    private final Object[] args;

    public LocalConsoleMessage(String key, Object... args) {
        this.key = key;
        this.args = args;
    }

    @Override
    public String getMessage(I18N i18N) {
        return i18N.get(key, args);
    }
}
