package com.alientome.game.entities.bars;

import com.alientome.core.graphics.GameGraphics;
import com.alientome.core.util.Colors;
import com.alientome.core.util.Logger;

import java.awt.*;
import java.awt.image.BufferedImage;

public class StatusBar {

    private static final Logger log = Logger.get();

    private final Color bgColor;
    private final FillColorProvider fillColor;
    private final BufferedImage icon;
    private final int width;
    private final int height;
    private final boolean hideIfZero;
    private final StatusValue value;

    public StatusBar(Color bgColor, FillColorProvider fillColor, BufferedImage icon, int width, int height, boolean hideIfZero, StatusValue value) {

        this.bgColor = bgColor;
        this.fillColor = fillColor;
        this.icon = icon;
        this.width = width;
        this.height = height;
        this.hideIfZero = hideIfZero;
        this.value = value;

        if (icon == null)
            log.w("Null icon");
    }

    public StatusBar(FillColorProvider fillColor, BufferedImage icon, int height, boolean hideIfZero, StatusValue value) {
        this(Colors.STATUS_BAR_DEF_BG, fillColor, icon, 30, height, hideIfZero, value);
    }

    public void draw(GameGraphics gg, int centerX, int y) {

        float percentValue = value.percentValue(gg.interpolation);

        /*if (percentValue <= 0 && hideIfZero)
            return;*/

        int x = centerX - width / 2;

        Graphics g = gg.graphics;
        g.setColor(bgColor);
        g.fillRect(x, y, width, height);

        if (icon != null)
            g.drawImage(icon, x - icon.getWidth(), y, null);

        g.setColor(fillColor.fillColorFor(percentValue));
        g.fillRect(x + 1, y + 1, (int) (percentValue * (width - 2)), height - 2);
    }

    public int getHeight() {
        return height;
    }

    public boolean willDraw(double interpolation) {
        return !(value.percentValue(interpolation) <= 0 && hideIfZero);
    }
}
