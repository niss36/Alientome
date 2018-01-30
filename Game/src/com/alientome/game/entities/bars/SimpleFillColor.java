package com.alientome.game.entities.bars;

import java.awt.*;

public class SimpleFillColor implements FillColorProvider {

    private final Color fillColor;

    public SimpleFillColor(Color fillColor) {
        this.fillColor = fillColor;
    }

    @Override
    public Color fillColorFor(float percentValue) {
        return fillColor;
    }
}
