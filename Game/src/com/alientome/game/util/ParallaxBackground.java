package com.alientome.game.util;

import com.alientome.core.collisions.AxisAlignedBoundingBox;
import com.alientome.core.graphics.GameGraphics;

import java.awt.*;
import java.util.List;

public class ParallaxBackground {

    private final List<Layer> layers;
    private final int yOffset;

    public ParallaxBackground(List<Layer> layers, int yOffset) {
        this.layers = layers;
        this.yOffset = yOffset;
    }

    public boolean isEmpty() {
        return layers.isEmpty();
    }

    public void draw(GameGraphics g, Rectangle clipBounds, AxisAlignedBoundingBox levelBounds) {

        int x = (int) levelBounds.getWidth() - g.origin.x;
        int y = yOffset - (int) levelBounds.getHeight() - g.origin.y;

        for (Layer layer : layers)
            layer.draw(g.graphics, clipBounds, x, y);
    }
}
