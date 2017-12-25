package com.alientome.game.commands.exceptions;

import com.alientome.game.util.Selector;

public class InvalidSelectorException extends CommandException {

    public InvalidSelectorException(String selector) {
        super("commands.generic.selector.invalid", selector);
    }

    public InvalidSelectorException(Selector selector) {
        this(selector.toString());
    }
}
