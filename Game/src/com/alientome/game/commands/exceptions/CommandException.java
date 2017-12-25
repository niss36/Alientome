package com.alientome.game.commands.exceptions;

import com.alientome.core.SharedInstances;
import com.alientome.core.SharedNames;
import com.alientome.core.internationalization.I18N;

public abstract class CommandException extends Exception {

    protected final Object[] errorArgs;

    public CommandException(String message, Object... errorArgs) {
        super(message);

        this.errorArgs = errorArgs;
    }

    @Override
    public String getLocalizedMessage() {
        return getLocalizedMessage(SharedInstances.get(SharedNames.I18N));
    }

    public String getLocalizedMessage(I18N i18N) {
        return i18N.get(getMessage(), errorArgs);
    }
}
