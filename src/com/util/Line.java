package com.util;

import java.awt.*;
import java.awt.geom.Point2D.Double;

public class Line {

    private final Double point1;
    private final Double point2;

    public boolean see;

    public Line(Double point1, Double point2) {

        this.point1 = point1;
        this.point2 = point2;
    }

    Double getPoint1() {
        return point1;
    }

    Double getPoint2() {
        return point2;
    }

    public void draw(Graphics g, Point origin) {

        if (see) g.setColor(Color.green);
        else g.setColor(Color.red);
        g.drawLine((int) point1.x - origin.x, (int) point1.y - origin.y, (int) point2.x - origin.x, (int) point2.y - origin.y);
    }
}
