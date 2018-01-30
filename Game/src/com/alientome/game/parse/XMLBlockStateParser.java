package com.alientome.game.parse;

@FunctionalInterface
public interface XMLBlockStateParser<STATE> {

    STATE parse(String id, String meta);
}
