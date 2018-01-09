package com.alientome.game.commands.messages;

import com.alientome.core.internationalization.I18N;
import com.alientome.game.commands.exceptions.CommandException;

public class ExceptionMessage implements ConsoleMessage {

    private final CommandException exception;

    public ExceptionMessage(CommandException exception) {
        this.exception = exception;
    }

    @Override
    public String getMessage(I18N i18N) {
        return exception.getLocalizedMessage(i18N);
    }
}
