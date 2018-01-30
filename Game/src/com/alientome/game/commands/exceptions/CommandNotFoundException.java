package com.alientome.game.commands.exceptions;

public class CommandNotFoundException extends CommandException {

    public CommandNotFoundException(String command) {
        super("commands.generic.notFound", command);
    }
}
