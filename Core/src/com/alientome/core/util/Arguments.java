package com.alientome.core.util;

import java.util.*;

public class Arguments {

    private final List<String> readonlyRawArgs;
    private final List<String> readonlyUnnamedArgs;
    private final Map<String, String> readonlyNamedArgs;

    public Arguments(String[] args) {

        List<String> rawArgs = new ArrayList<>();
        List<String> unnamedArgs = new ArrayList<>();
        Map<String, String> namedArgs = new HashMap<>();

        for (String arg : args) {

            rawArgs.add(arg);

            if (isNamed(arg)) {
                int i = arg.indexOf('=');
                String key = arg.substring(2, i);
                String value = arg.substring(i + 1);
                namedArgs.put(key, value);
            } else
                unnamedArgs.add(arg);
        }

        readonlyRawArgs = Collections.unmodifiableList(rawArgs);
        readonlyUnnamedArgs = Collections.unmodifiableList(unnamedArgs);
        readonlyNamedArgs = Collections.unmodifiableMap(namedArgs);
    }

    private boolean isValidFirstChar(char c) {
        return Character.isLetter(c) || c == '_';
    }

    private boolean isNamed(String arg) {

        return arg.startsWith("--") && arg.indexOf('=') > 2 && isValidFirstChar(arg.charAt(2));
    }

    public List<String> getRaw() {
        return readonlyRawArgs;
    }

    public List<String> getUnnamed() {
        return readonlyUnnamedArgs;
    }

    public Map<String, String> getNamed() {
        return readonlyNamedArgs;
    }
}
