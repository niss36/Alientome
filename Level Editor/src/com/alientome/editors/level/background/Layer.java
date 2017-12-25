package com.alientome.editors.level.background;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Layer {

    public String name;
    public BufferedImage image;
    public double xCoef;
    public double yCoef;

    public Layer(String name, BufferedImage image, double xCoef, double yCoef) {
        this.name = name;
        this.image = image;
        this.xCoef = xCoef;
        this.yCoef = yCoef;
    }

    public void draw(Graphics g, Rectangle clipBounds, int x, double scale) {

        int imgWidth = (int) (image.getWidth() * scale);
        int imgHeight = (int) (image.getHeight() * scale);

        if (xCoef == 0)
            g.drawImage(image, 0, 0, imgWidth, imgHeight, null);
        else {

            int resolvedX = (int) (x * xCoef) % clipBounds.width;

            if (resolvedX == 0)
                g.drawImage(image, 0, 0, imgWidth, imgHeight, null);
            else {
                g.drawImage(image, resolvedX - imgWidth, 0, imgWidth, imgHeight, null);
                g.drawImage(image, resolvedX, 0, imgWidth, imgHeight, null);
            }
        }
    }
}
