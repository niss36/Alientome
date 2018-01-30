package com.alientome.game.parse;

import java.util.Map;

@FunctionalInterface
public interface XMLEntityParser {

    void parse(String id, int x, int y, Map<String, String> tags);
}
