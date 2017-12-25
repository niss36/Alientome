package com.alientome.game.commands.exceptions;

public class UnknownErrorException extends CommandException {

    public UnknownErrorException(Throwable cause) {
        super("commands.generic.exception", cause);

        initCause(cause);
    }
}
