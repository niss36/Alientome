package com.alientome.game.parse;

@FunctionalInterface
public interface MapBlockParser {

    void parse(int x, int y, int stateIndex);
}
