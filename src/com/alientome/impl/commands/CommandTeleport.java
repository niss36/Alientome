package com.alientome.impl.commands;

import com.alientome.core.vecmath.Vec2;
import com.alientome.game.commands.Command;
import com.alientome.game.commands.CommandSender;
import com.alientome.game.commands.exceptions.CommandException;
import com.alientome.game.commands.exceptions.UsageException;
import com.alientome.game.commands.messages.ConsoleMessage;
import com.alientome.game.commands.messages.Messages;
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

        ConsoleMessage message;
        Entity teleported;
        Vec2 destination;

        if (args.length == 1) {
            teleported = sender.getEntity();

            Entity target = level.selectFirst(from(args[0]));
            destination = target.getPos();

            message = Messages.entityAware("commands.tp.teleportedToEntity", teleported, target);

        } else if (args.length == 2) {

            if (args[0].startsWith("@")) {
                teleported = level.selectFirst(from(args[0]));

                Entity target = level.selectFirst(from(args[1]));
                destination = target.getPos();

                message = Messages.entityAware("commands.tp.teleportedToEntity", teleported, target);
            } else {
                teleported = sender.getEntity();

                double x = Command.parseDouble(args[0]);
                double y = Command.parseDouble(args[1]);
                destination = new Vec2(x, y);

                message = Messages.entityAware("commands.tp.teleportedToXY", teleported, x, y);
            }
        } else if (args.length == 3) {

            teleported = level.selectFirst(from(args[0]));

            double x = Command.parseDouble(args[1]);
            double y = Command.parseDouble(args[2]);
            destination = new Vec2(x, y);

            message = Messages.entityAware("commands.tp.teleportedToXY", teleported, x, y);
        } else
            throw new UsageException(this);

        teleported.getPos().set(destination);
        sender.addConsoleMessage(message);
    }
}
