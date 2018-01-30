package com.alientome.game.util;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Layer {

    private final BufferedImage image;
    private final double xCoefficient;
    private final double yCoefficient;

    public Layer(BufferedImage image, double xCoefficient, double yCoefficient) {
        this.image = image;
        this.xCoefficient = xCoefficient;
        this.yCoefficient = yCoefficient;
    }

    public void draw(Graphics g, Rectangle clipBounds, int x, int y) {

        if (xCoefficient == 0 && yCoefficient == 0)
            g.drawImage(image, 0, 0, null);
        else {

            int resolvedX = (int) (x * xCoefficient) % clipBounds.width;
            int resolvedY = (int) (y * yCoefficient);

            if (resolvedX == 0)
                g.drawImage(image, 0, resolvedY, null);
            else {
                BufferedImage sub1, sub2;
                sub1 = image.getSubimage(clipBounds.width - resolvedX, 0, resolvedX, clipBounds.height);
                sub2 = image.getSubimage(0, 0, clipBounds.width - resolvedX, clipBounds.height);
                g.drawImage(sub1, 0, resolvedY, null);
                g.drawImage(sub2, resolvedX, resolvedY, null);
            }
        }
    }
}
