package com.alientome.impl.commands;

import com.alientome.game.commands.Command;
import com.alientome.game.commands.CommandSender;
import com.alientome.game.commands.exceptions.CommandException;
import com.alientome.game.commands.messages.LocalConsoleMessage;
import com.alientome.game.commands.messages.Messages;
import com.alientome.game.entities.Entity;
import com.alientome.game.util.Selector;

import java.util.List;

public class CommandKill implements Command {

    @Override
    public String getCommandName() {
        return "kill";
    }

    @Override
    public void processCommand(CommandSender sender, String[] args) throws CommandException {

        if (args.length == 0) {
            sender.getEntity().setDead();
            sender.addConsoleMessage(Messages.entityAware("commands.kill.killed", sender.getEntity()));

        } else if (args.length == 1) {

            Selector selector = Selector.from(args[0]);

            List<Entity> entities = sender.getLevel().selectAll(selector);

            int numKilled = entities.size();
            entities.forEach(Entity::setDead);

            if (numKilled == 1)
                sender.addConsoleMessage(Messages.entityAware("commands.kill.killed", entities.get(0)));
            else
                sender.addConsoleMessage(new LocalConsoleMessage("commands.kill.killedNum", numKilled));
        }
    }
}
