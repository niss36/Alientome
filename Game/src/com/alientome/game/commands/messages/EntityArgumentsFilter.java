package com.alientome.game.commands.messages;

import com.alientome.core.internationalization.I18N;
import com.alientome.game.entities.Entity;

public class EntityArgumentsFilter implements ArgumentsFilter {

    @Override
    public boolean filter(Object arg) {
        return arg instanceof Entity;
    }

    @Override
    public Object apply(Object arg, I18N i18N) {

        //It is assumed that the method #filter(Object arg) has been evaluated to true on the given argument.
        //That is to say that arg is an instance of Entity

        Entity entity = (Entity) arg;

        String name;
        if (entity.hasCustomName())
            name = entity.getCustomName();
        else
            name = i18N.get(entity.getNameKey());

        return name;
    }
}
