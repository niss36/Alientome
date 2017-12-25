package com.alientome.game.commands;

import com.alientome.game.commands.exceptions.CommandException;
import com.alientome.game.commands.exceptions.InvalidNumberException;

public interface Command {

    static int parseInt(String arg) throws CommandException {

        try {
            return Integer.parseInt(arg);
        } catch (NumberFormatException e) {
            throw new InvalidNumberException(arg);
        }
    }

    static double parseDouble(String arg) throws CommandException {

        try {
            return Double.parseDouble(arg);
        } catch (NumberFormatException e) {
            throw new InvalidNumberException(arg);
        }
    }

    static float parseFloat(String arg) throws CommandException {

        try {
            return Float.parseFloat(arg);
        } catch (NumberFormatException e) {
            throw new InvalidNumberException(arg);
        }
    }

    String getCommandName();

    void processCommand(CommandSender sender, String[] args) throws CommandException;
}
