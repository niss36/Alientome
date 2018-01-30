package com.alientome.game.util;

import com.alientome.game.commands.exceptions.CommandException;
import com.alientome.game.commands.exceptions.InvalidNumberException;
import com.alientome.game.commands.exceptions.InvalidSelectorException;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.alientome.game.util.SelectorType.*;

public class Selector {

    private static final Pattern selectorPattern = Pattern.compile("\\s*@(?:(p)|(c)|(a(?:\\[(.+)])?))\\s*");
    private static final Pattern argsSplitter = Pattern.compile("\\s*,\\s*");
    private static final Pattern indexPattern = Pattern.compile("(?:index\\s*=\\s*)?([^=]+)");
    private static final Pattern argPattern = Pattern.compile("(\\w+)\\s*=\\s*(\\w+)");

    public final SelectorType type;
    public final Map<String, String> args;

    private Selector(SelectorType type, Map<String, String> args) {
        this.type = type;
        this.args = args;
    }

    private Selector(SelectorType type) {
        this(type, null);
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("@");
        switch (type) {
            case PLAYER:
                s.append("p");
                break;

            case CONTROLLED:
                s.append("c");
                break;

            case ALL:
                s.append("a");
                break;

            default:
                s.append("<UNKNOWN>");
                break;
        }

        s.append('[');

        for (Map.Entry<String, String> entry : args.entrySet())
            s.append(entry.getKey()).append("=").append(entry.getValue());

        s.append(']');

        return s.toString();
    }

    public static Selector from(String s) throws CommandException {

        Matcher matcher = selectorPattern.matcher(s);

        if (matcher.find()) {

            if (matcher.group(1) != null)
                return new Selector(PLAYER);
            if (matcher.group(2) != null)
                return new Selector(CONTROLLED);
            String e = matcher.group(3);
            if (e != null)
                return new Selector(ALL, parseArgs(matcher.group(4)));
        }

        throw new InvalidSelectorException(s);
    }

    private static Map<String, String> parseArgs(String source) throws CommandException {

        if (source == null)
            source = "";

        source = source.trim();

        String[] split = argsSplitter.split(source);

        Map<String, String> args = new HashMap<>(split.length);

        for (int i = 0; i < split.length; i++) {

            if (i == 0) {
                Matcher indexMatcher = indexPattern.matcher(split[0]);
                if (indexMatcher.find()) {
                    String index = indexMatcher.group(1);

                    for (int j = 0; j < index.length(); j++)
                        if (!Character.isDigit(index.charAt(j)))
                            throw new InvalidNumberException(index);

                    args.put("index", index);
                    continue;
                }
            }

            Matcher argMatcher = argPattern.matcher(split[i]);
            if (argMatcher.find())
                args.put(argMatcher.group(1), argMatcher.group(2));
        }

        return args;
    }
}
