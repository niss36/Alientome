package com.alientome.impl.commands;

import com.alientome.core.util.Direction;
import com.alientome.game.commands.Command;
import com.alientome.game.commands.CommandSender;
import com.alientome.game.commands.exceptions.CommandException;
import com.alientome.game.commands.exceptions.UsageException;
import com.alientome.game.control.Control;
import com.alientome.game.control.Controller;

public class CommandFly implements Command {

    @Override
    public String getCommandName() {
        return "fly";
    }

    @Override
    public void processCommand(CommandSender sender, String[] args) throws CommandException {

        if (args.length == 1) {

            switch (args[0]) {
                case "on":

                    Controller controller = new Controller(sender.getEntity());

                    controller.addControl(Control.createMoveControl(sender.getEntity(), "moveLeft", Direction.LEFT));
                    controller.addControl(Control.createMoveControl(sender.getEntity(), "moveRight", Direction.RIGHT));
                    controller.addControl(Control.createMoveControl(sender.getEntity(), "jump", Direction.UP));

                    sender.getEntity().setAffectedByGravity(false);
                    sender.getLevel().setController(controller);
                    break;

                case "off":

                    sender.getEntity().setAffectedByGravity(true);
                    sender.getLevel().setControlled(sender.getEntity());
                    break;

                default:
                    throw new UsageException(this);
            }

        } else throw new UsageException(this);
    }
}
