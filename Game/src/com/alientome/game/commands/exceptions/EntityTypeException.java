package com.alientome.game.commands.exceptions;

public class EntityTypeException extends CommandException {

    public EntityTypeException(String expected, String got) {
        super("commands.generic.entity.invalid", expected, got);
    }
}
