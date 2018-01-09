package com.alientome.game.commands;

import com.alientome.core.SharedInstances;
import com.alientome.core.util.Logger;
import com.alientome.game.commands.exceptions.CommandException;
import com.alientome.game.commands.exceptions.CommandNotFoundException;
import com.alientome.game.commands.exceptions.UnknownErrorException;
import com.alientome.game.commands.messages.ExceptionMessage;
import com.alientome.game.registry.GameRegistry;

import java.util.*;

import static com.alientome.core.SharedNames.REGISTRY;

public class CommandHandler {

    private static final Logger log = Logger.get();
    private final List<CommandExecutionInfo> delayedExecutions = new ArrayList<>();
    private final Map<String, Command> commands = new HashMap<>();

    public CommandHandler() {

        GameRegistry registry = SharedInstances.get(REGISTRY);

        List<Command> commandsRegistry = registry.getCommandsRegistry();

        for (Command command : commandsRegistry)
            commands.put(command.getCommandName(), command);
    }

    public void executeAll() {

        synchronized (delayedExecutions) {
            for (CommandExecutionInfo executionInfo : delayedExecutions)
                try {
                    executionInfo.execute();
                } catch (CommandException e) {
                    executionInfo.sender.addConsoleMessage(new ExceptionMessage(e));
                    log.e("Exception handling command '" + executionInfo + "' :");
                    e.printStackTrace();
                }

            delayedExecutions.clear();
        }
    }

    public void queueCommand(String commandID, CommandSender sender, String[] args) throws CommandException {

        Command command = commands.get(commandID);
        if (command == null)
            throw new CommandNotFoundException(commandID);

        synchronized (delayedExecutions) {
            delayedExecutions.add(new CommandExecutionInfo(command, sender, args));
        }
    }

    private class CommandExecutionInfo {

        private final Command command;
        private final CommandSender sender;
        private final String[] args;

        private CommandExecutionInfo(Command command, CommandSender sender, String[] args) {

            this.command = command;
            this.sender = sender;
            this.args = args;
        }

        private void execute() throws CommandException {

            try {
                command.processCommand(sender, args);
            } catch (CommandException e) {
                throw e;
            } catch (Exception e) {
                throw new UnknownErrorException(e);
            }
        }

        @Override
        public String toString() {
            String argsStr = Arrays.toString(args);
            argsStr = argsStr.substring(1, argsStr.length() - 1);
            return "/" + command.getCommandName() + " " + argsStr;
        }
    }
}
