package com.game;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public interface BlockConstants {

    Map<Integer, Byte> rgbConvert = new HashMap<>();
    byte VOID = Byte.MIN_VALUE;
    byte AIR = 0;
    byte SAND = 1;
    byte HOLE = 2;

    static void init() {
        rgbConvert.put(new Color(0x0094FF).getRGB(), AIR);
        rgbConvert.put(new Color(0xFFD800).getRGB(), SAND);
        rgbConvert.put(new Color(0x000000).getRGB(), HOLE);
    }
}
