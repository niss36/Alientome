package com.alientome.game.background;

import com.alientome.core.collisions.AxisAlignedBoundingBox;
import com.alientome.core.graphics.GameGraphics;

import java.awt.*;
import java.util.List;

public class ParallaxBackground {

    private final List<Layer> layers;

    public ParallaxBackground(List<Layer> layers) {
        this.layers = layers;
    }

    public boolean isEmpty() {
        return layers.isEmpty();
    }

    public void draw(GameGraphics g, Rectangle clipBounds, AxisAlignedBoundingBox levelBounds) {

        int x = (int) levelBounds.getWidth() - g.origin.x - clipBounds.width / 2; //Need to reverse direction. Take origin to be the center of the viewport.
        int y = (int) levelBounds.getHeight() - g.origin.y - clipBounds.height / 2; //Same

        for (Layer layer : layers)
            layer.draw(g.graphics, clipBounds, x, y);
    }
}
