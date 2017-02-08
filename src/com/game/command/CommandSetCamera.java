package com.game.command;

import com.game.entities.Entity;

public class CommandSetCamera extends Command {

    @Override
    public String getCommandName() {
        return "setCamera";
    }

    @Override
    public void processCommand(CommandSender sender, String[] args) {

        if (args.length == 1) {

            Entity target = sender.getLevel().parseSelectorFirst(args[0]);
            sender.getLevel().setCamera(target);
        }
    }
}
