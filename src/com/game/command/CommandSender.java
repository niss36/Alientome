package com.game.command;

import com.game.entities.Entity;
import com.game.level.Level;
import com.util.Vec2;

public interface CommandSender {

    Vec2 getPos();

    Level getLevel();

    Entity getEntity();

    void addChatMessage(String message);
}
