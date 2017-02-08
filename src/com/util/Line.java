package com.util;

import com.util.visual.GameGraphics;

import java.awt.*;

public class Line {

    private final Vec2 pos1;
    private final Vec2 pos2;

    public boolean see;

    public Line(Vec2 pos1, Vec2 pos2) {

        this.pos1 = pos1;
        this.pos2 = pos2;
    }

    public Vec2 getPos1() {
        return pos1;
    }

    public Vec2 getPos2() {
        return pos2;
    }

    public void draw(GameGraphics g) {

        g.graphics.setColor(see ? Color.green : Color.red);
        g.graphics.drawLine((int) pos1.x - g.origin.x, (int) pos1.y - g.origin.y, (int) pos2.x - g.origin.x, (int) pos2.y - g.origin.y);
    }
}
