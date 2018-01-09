package com.alientome.game.commands.messages;

public class Messages {

    public static ConsoleMessage entityAware(String key, Object... args) {
        return new FilteredConsoleMessage(key, new EntityArgumentsFilter(), args);
    }
}
