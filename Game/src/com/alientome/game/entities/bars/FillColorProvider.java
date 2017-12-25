package com.alientome.game.entities.bars;

import java.awt.*;

@FunctionalInterface
public interface FillColorProvider {

    Color fillColorFor(float percentValue);
}
