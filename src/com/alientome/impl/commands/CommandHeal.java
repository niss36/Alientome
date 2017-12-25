package com.alientome.impl.commands;

import com.alientome.game.commands.Command;
import com.alientome.game.commands.CommandSender;
import com.alientome.game.commands.EntityConsoleMessage;
import com.alientome.game.commands.LocalConsoleMessage;
import com.alientome.game.commands.exceptions.CommandException;
import com.alientome.game.commands.exceptions.EntityTypeException;
import com.alientome.game.commands.exceptions.SelectorNotFoundException;
import com.alientome.game.commands.exceptions.UsageException;
import com.alientome.game.entities.Entity;
import com.alientome.game.entities.EntityLiving;
import com.alientome.game.util.Selector;

import java.util.List;

public class CommandHeal implements Command {

    @Override
    public String getCommandName() {
        return "heal";
    }

    @Override
    public void processCommand(CommandSender sender, String[] args) throws CommandException {

        if (args.length == 1) {

            Entity entity = sender.getEntity();
            float value = Command.parseFloat(args[0]);

            if (entity instanceof EntityLiving) {
                ((EntityLiving) entity).heal(value);
                sender.addConsoleMessage(new EntityConsoleMessage("commands.heal.healed", entity));
            } else throw new EntityTypeException("EntityLiving", entity.getClass().getName());

        } else if (args.length == 2) {

            Selector selector = Selector.from(args[0]);

            List<Entity> entities = sender.getLevel().selectAll(selector);
            float value = Command.parseFloat(args[1]);

            int numHealed = 0;
            Entity healed = null;

            for (Entity entity : entities)
                if (entity instanceof EntityLiving) {
                    ((EntityLiving) entity).heal(value);
                    if (numHealed++ == 0)
                        healed = entity;
                    else
                        healed = null;
                }

            if (numHealed == 0)
                throw new SelectorNotFoundException(selector);
            else if (numHealed == 1)
                sender.addConsoleMessage(new EntityConsoleMessage("commands.heal.healed", healed));
            else
                sender.addConsoleMessage(new LocalConsoleMessage("commands.heal.healedNum", numHealed));

        } else throw new UsageException(this);
    }
}
