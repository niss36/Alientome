package com.alientome.game.commands.messages;

import com.alientome.core.internationalization.I18N;

public class FilteredConsoleMessage implements ConsoleMessage {

    private final String key;
    private final ArgumentsFilter filter;
    private final Object[] args;

    public FilteredConsoleMessage(String key, ArgumentsFilter filter, Object... args) {

        this.key = key;
        this.filter = filter;
        this.args = args;
    }

    @Override
    public String getMessage(I18N i18N) {
        Object[] filtered = new Object[args.length];

        for (int i = 0; i < args.length; i++) {
            if (filter.filter(args[i]))
                filtered[i] = filter.apply(args[i], i18N);
            else
                filtered[i] = args[i];
        }

        return i18N.get(key, filtered);
    }
}
