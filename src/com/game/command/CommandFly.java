package com.game.command;

import com.game.control.Control;
import com.game.control.Controller;
import com.util.Direction;

public class CommandFly extends Command {

    @Override
    public String getCommandName() {
        return "fly";
    }

    @Override
    public void processCommand(CommandSender sender, String[] args) {

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
                    throw new IllegalArgumentException("Illegal usage");
            }

        } else throw new IllegalArgumentException("Illegal usage");
    }
}
