package com.alientome.impl.commands;

import com.alientome.game.commands.Command;
import com.alientome.game.commands.CommandSender;
import com.alientome.game.commands.exceptions.CommandException;

public class CommandDamage implements Command {

    @Override
    public String getCommandName() {
        return "damage";
    }

    @Override
    public void processCommand(CommandSender sender, String[] args) throws CommandException {

    }
}
