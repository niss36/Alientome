package com.alientome.game.parse;

import java.awt.image.BufferedImage;
import java.io.IOException;

@FunctionalInterface
public interface ImageParser {

    BufferedImage parse(String path, int scale) throws IOException;
}
