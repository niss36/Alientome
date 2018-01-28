package com.alientome.game.commands.exceptions;

import com.alientome.core.Context;
import com.alientome.game.commands.Command;

public class UsageException extends CommandException {

    public UsageException(Command command, Context context) {
        super("commands.generic.usage", context.getI18N().get("commands." + command.getCommandName() + ".usage"));
    }
}
