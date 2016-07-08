package com.game.blocks;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public interface BlockConstants {

    Map<Integer, Byte> rgbConvert = new HashMap<>();
    byte VOID = Byte.MIN_VALUE;
    byte AIR = 0;
    byte SAND = 1;
    byte HOLE = 2;
    byte PILLAR = 3;
    byte SAND_BACKGROUND = 4;

    static void init() {
        rgbConvert.put(new Color(0x0094FF).getRGB(), AIR);
        rgbConvert.put(new Color(0xFFD800).getRGB(), SAND);
        rgbConvert.put(new Color(0x000000).getRGB(), HOLE);
        rgbConvert.put(new Color(0x808080).getRGB(), PILLAR);
        rgbConvert.put(new Color(0xFF6A00).getRGB(), SAND_BACKGROUND);
    }
}
