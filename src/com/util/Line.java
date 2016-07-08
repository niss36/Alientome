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

    public Double getPoint1() {
        return point1;
    }

    public Double getPoint2() {
        return point2;
    }

    public void draw(Graphics g, Point min) {

        if (see) g.setColor(Color.green);
        else g.setColor(Color.red);
        g.drawLine((int) point1.x - min.x, (int) point1.y - min.y, (int) point2.x - min.x, (int) point2.y - min.y);
    }
}
