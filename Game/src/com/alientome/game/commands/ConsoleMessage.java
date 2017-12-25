package com.alientome.game.commands;

import com.alientome.core.internationalization.I18N;

@FunctionalInterface
public interface ConsoleMessage {

    String getMessage(I18N i18N);
}
