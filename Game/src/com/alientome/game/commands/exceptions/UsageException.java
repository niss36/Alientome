package com.alientome.game.commands.exceptions;

import com.alientome.core.SharedInstances;
import com.alientome.core.SharedNames;
import com.alientome.core.internationalization.I18N;
import com.alientome.game.commands.Command;

public class UsageException extends CommandException {

    public UsageException(Command command) {

        super("commands.generic.usage",
                SharedInstances.<I18N>get(SharedNames.I18N).get("commands." + command.getCommandName() + ".usage"));
    }
}
