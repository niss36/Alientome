package com.alientome.visual.animations;

import com.alientome.core.util.Direction;
import com.alientome.visual.Visual;

import java.awt.*;
import java.awt.image.BufferedImage;

public interface Animation extends Visual {

    void draw(Graphics g, int x, int y, Direction direction, int imageUsed);

    int getLength();

    BufferedImage[] getSprites();

    int getDelay();

    int[] getXOffsets();

    int[] getYOffsets();

    boolean isLoop();
}
