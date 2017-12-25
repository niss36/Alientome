package com.alientome.game.commands;

import com.alientome.core.internationalization.I18N;
import com.alientome.game.entities.Entity;

public class EntityConsoleMessage implements ConsoleMessage {

    private final String key;
    private final Entity entity;

    public EntityConsoleMessage(String key, Entity entity) {
        this.key = key;
        this.entity = entity;
    }

    @Override
    public String getMessage(I18N i18N) {
        String name;
        if (entity.hasCustomName())
            name = entity.getCustomName();
        else
            name = i18N.get(entity.getNameKey());
        return i18N.get(key, name);
    }
}
