package com.alientome.game.commands.messages;

import com.alientome.core.internationalization.I18N;

public interface ArgumentsFilter {

    boolean filter(Object arg);

    Object apply(Object arg, I18N i18N);
}
