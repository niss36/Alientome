package com.alientome.impl.commands;

import com.alientome.game.commands.Command;
import com.alientome.game.commands.CommandSender;
import com.alientome.game.commands.exceptions.CommandException;
import com.alientome.game.commands.exceptions.UsageException;
import com.alientome.game.entities.Entity;
import com.alientome.game.util.Selector;

public class CommandSetControlled implements Command {

    @Override
    public String getCommandName() {
        return "setControlled";
    }

    @Override
    public void processCommand(CommandSender sender, String[] args) throws CommandException {

        if (args.length == 1) {

            Entity target = sender.getLevel().selectFirst(Selector.from(args[0]));
            sender.getLevel().setControlled(target);
        } else
            throw new UsageException(this);
    }
}
