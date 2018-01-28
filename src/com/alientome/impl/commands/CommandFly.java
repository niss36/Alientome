package com.alientome.impl.commands;

import com.alientome.core.util.Direction;
import com.alientome.game.commands.Command;
import com.alientome.game.commands.CommandSender;
import com.alientome.game.commands.exceptions.CommandException;
import com.alientome.game.commands.exceptions.UsageException;
import com.alientome.game.control.Control;
import com.alientome.game.control.Controller;
import com.alientome.game.entities.Entity;
import com.alientome.game.level.Level;

public class CommandFly implements Command {

    @Override
    public String getCommandName() {
        return "fly";
    }

    @Override
    public void processCommand(CommandSender sender, String[] args) throws CommandException {

        Entity entity = sender.getEntity();
        Level level = sender.getLevel();

        if (args.length == 1) {
            switch (args[0]) {
                case "on":

                    Controller controller = entity.newController();

                    controller.addControlOverride(Control.createMoveControl(entity, "jump", Direction.UP));

                    entity.setAffectedByGravity(false);
                    level.setController(controller);
                    break;

                case "off":

                    entity.setAffectedByGravity(true);
                    level.setControlled(entity);
                    break;

                default:
                    throw new UsageException(this, level.getContext());
            }

        } else
            throw new UsageException(this, level.getContext());
    }
}
