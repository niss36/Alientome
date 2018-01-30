package com.alientome.editors.level.background;

import java.awt.*;
import java.util.List;

public class Background {

    public List<Layer> layers;
    public int scale;
    public int yOffset;

    public Background(List<Layer> layers, int scale, int yOffset) {
        this.layers = layers;
        this.scale = scale;
        this.yOffset = yOffset;
    }

    public void draw(Graphics g, double interpolation) {

        Rectangle clipBounds = g.getClipBounds();

        double minXCoef = Double.POSITIVE_INFINITY;

        for (Layer layer : layers)
            if (layer.xCoef > 0 && layer.xCoef < minXCoef)
                minXCoef = layer.xCoef;
        double x = interpolation * clipBounds.width / minXCoef;

        for (Layer layer : layers)
            layer.draw(g, clipBounds, (int) x, scale / 2d);
    }
}
