package com.alientome.game.parse;

import java.awt.image.BufferedImage;

@FunctionalInterface
public interface XMLLayerParser<LAYER> {

    LAYER parse(double xCoef, double yCoef, String src, BufferedImage image);
}
