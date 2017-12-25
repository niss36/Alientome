package com.alientome.game.commands.exceptions;

public class InvalidNumberException extends CommandException {

    public InvalidNumberException(String arg) {

        super("commands.generic.number.invalid", arg);
    }
}
