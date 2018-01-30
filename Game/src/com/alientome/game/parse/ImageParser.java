package com.alientome.game.parse;

import java.awt.image.BufferedImage;

@FunctionalInterface
public interface ImageParser {

    BufferedImage parse(String path, int scale);
}
