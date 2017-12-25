package com.alientome.game.commands.exceptions;

import com.alientome.game.util.Selector;

public class SelectorNotFoundException extends CommandException {

    public SelectorNotFoundException(String selector) {
        super("commands.generic.selector.notFound", selector);
    }

    public SelectorNotFoundException(Selector selector) {
        this(selector.toString());
    }
}
