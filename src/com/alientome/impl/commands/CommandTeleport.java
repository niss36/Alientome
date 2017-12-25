package com.alientome.impl.commands;

import com.alientome.core.util.Vec2;
import com.alientome.game.commands.Command;
import com.alientome.game.commands.CommandSender;
import com.alientome.game.commands.ConsoleMessage;
import com.alientome.game.commands.LocalConsoleMessage;
import com.alientome.game.commands.exceptions.CommandException;
import com.alientome.game.commands.exceptions.UsageException;
import com.alientome.game.entities.Entity;
import com.alientome.game.level.Level;

import static com.alientome.game.util.Selector.from;

public class CommandTeleport implements Command {

    @Override
    public String getCommandName() {
        return "tp";
    }

    @Override
    public void processCommand(CommandSender sender, String[] args) throws CommandException {

        Level level = sender.getLevel();

        ConsoleMessage message = new LocalConsoleMessage("commands.teleport");
        Entity target;
        Vec2 destination;

        if (args.length == 1) {
            target = sender.getEntity();
            destination = level.selectFirst(from(args[0])).getPos();

        } else if (args.length == 2) {

            if (args[0].startsWith("@")) {
                target = level.selectFirst(from(args[0]));
                destination = level.selectFirst(from(args[1])).getPos();
            } else {
                target = sender.getEntity();
                double x = Command.parseDouble(args[0]);
                double y = Command.parseDouble(args[1]);
                destination = new Vec2(x, y);
            }
        } else if (args.length == 3) {

            target = level.selectFirst(from(args[0]));
            double x = Command.parseDouble(args[1]);
            double y = Command.parseDouble(args[2]);
            destination = new Vec2(x, y);
        } else
            throw new UsageException(this);

        target.getPos().set(destination);
        sender.addConsoleMessage(message);
    }
}
