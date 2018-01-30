package com.alientome.game.commands.exceptions;

import com.alientome.core.internationalization.I18N;
import com.alientome.game.commands.Command;

public class UsageException extends CommandException {

    public UsageException(Command command) {
        super("commands.generic.usage", "commands." + command.getCommandName() + ".usage");
    }

    @Override
    public String getLocalizedMessage(I18N i18N) {
        String usageKey = (String) errorArgs[0];
        return i18N.get(getMessage(), i18N.get(usageKey));
    }
}
