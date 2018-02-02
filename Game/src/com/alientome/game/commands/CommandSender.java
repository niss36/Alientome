package com.alientome.game.commands;

import com.alientome.core.vecmath.Vec2;
import com.alientome.game.commands.messages.ConsoleMessage;
import com.alientome.game.entities.Entity;
import com.alientome.game.level.Level;

public interface CommandSender {

    Vec2 getPos();

    Level getLevel();

    Entity getEntity();

    void addConsoleMessage(ConsoleMessage message);
}
