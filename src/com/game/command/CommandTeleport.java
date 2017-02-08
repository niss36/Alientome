package com.game.command;

import com.game.entities.Entity;
import com.util.Vec2;

import java.util.Arrays;

import static com.util.Util.isSelector;

public class CommandTeleport extends Command {

    @Override
    public String getCommandName() {
        return "tp";
    }

    @Override
    public void processCommand(CommandSender sender, String[] args) {

        String message = "Teleported ";
        Entity target;
        Vec2 destination;

        if (args.length == 1) {
            target = sender.getEntity();
            destination = sender.getLevel().parseSelectorFirst(args[0]).getPos();

        } else if (args.length == 2) {

            if (isSelector(args[0])) {
                target = sender.getLevel().parseSelectorFirst(args[0]);
                destination = sender.getLevel().parseSelectorFirst(args[1]).getPos();
            } else {
                target = sender.getEntity();
                double x = Double.parseDouble(args[0]);
                double y = Double.parseDouble(args[1]);
                destination = new Vec2(x, y);
            }
        } else if (args.length == 3) {

            target = sender.getLevel().parseSelectorFirst(args[0]);
            double x = Double.parseDouble(args[1]);
            double y = Double.parseDouble(args[2]);
            destination = new Vec2(x, y);
        } else
            throw new IllegalArgumentException("Illegal usage for tp : " + Arrays.toString(args));

        target.getPos().set(destination);
        sender.addChatMessage(message);
    }
}
