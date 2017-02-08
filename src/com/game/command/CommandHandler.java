package com.game.command;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.util.Util.parseXML;

public class CommandHandler {

    private final List<CommandExecutionInfo> delayedExecutions = new ArrayList<>();
    private final Map<String, Command> commands = new HashMap<>();

    public CommandHandler() {

        try {
            Element root = parseXML("commands");

            NodeList commandsList = root.getElementsByTagName("command");
            for (int i = 0; i < commandsList.getLength(); i++) {

                Element commandNode = (Element) commandsList.item(i);

                Class<?> commandClass = Class.forName("com.game.command." + commandNode.getAttribute("class"));

                Command command = (Command) commandClass.newInstance();

                commands.put(command.getCommandName(), command);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void executeAll() {

        synchronized (delayedExecutions) {
            delayedExecutions.forEach(CommandExecutionInfo::execute);

            delayedExecutions.clear();
        }
    }

    public void queueCommand(String commandID, CommandSender sender, String[] args) {

        synchronized (delayedExecutions) {
            delayedExecutions.add(new CommandExecutionInfo(commands.get(commandID), sender, args));
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

        private void execute() {

            try {
                command.processCommand(sender, args);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
