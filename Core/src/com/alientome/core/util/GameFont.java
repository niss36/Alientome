package com.alientome.core.util;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;

public class GameFont {

    private static final Font FONT;

    static {
        // Default Font in case things go wrong
        Font f = new Font("Courier", Font.PLAIN, 10);

        try (InputStream stream = ClassLoader.getSystemResourceAsStream("Font/Alientome.ttf")) {
            f = Font.createFont(Font.TRUETYPE_FONT, stream);
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
        }
        FONT = f;
    }

    public static Font get(int scale) {
        if (scale <= 0)
            throw new IllegalArgumentException("Scale (" + scale + ") must be positive !");
        return FONT.deriveFont(10f * scale);
    }
}
