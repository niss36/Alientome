package com.game.command;

public abstract class Command {

    public abstract String getCommandName();

    public abstract void processCommand(CommandSender sender, String[] args);
}
