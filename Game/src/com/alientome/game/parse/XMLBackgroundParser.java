package com.alientome.game.parse;

import java.util.List;

@FunctionalInterface
public interface XMLBackgroundParser<BACKGROUND, LAYER> {

    BACKGROUND parse(List<LAYER> layers, int yOffset, int scale);
}
