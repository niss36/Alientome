package com.util.visual;

import java.awt.*;

public class GameGraphics {

    public final Graphics graphics;
    public final Point origin;
    public final long currentTimeTicks;
    public final double interpolation;

    public GameGraphics(Graphics graphics, Point origin, long currentTimeTicks, double interpolation) {
        this.graphics = graphics;
        this.origin = origin;
        this.currentTimeTicks = currentTimeTicks;
        this.interpolation = interpolation;
    }
}
