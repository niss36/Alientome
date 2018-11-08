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
            g.drawImage(image, 0, 0, null); //Fixed background : simply draw image.
        else {

            int resolvedX = (int) (x * xCoefficient) % clipBounds.width; //Scale and wrap around on x axis
            int resolvedY = (int) (y * yCoefficient); //Just scale on y axis

            if (resolvedX == 0)
                g.drawImage(image, 0, resolvedY, null); //No need to wrap around; should fit perfectly on screen
            else {
                BufferedImage sub1, sub2; //Create sub images to implement wrap around.
                sub1 = image.getSubimage(clipBounds.width - resolvedX, 0, resolvedX, clipBounds.height); //Left part
                sub2 = image.getSubimage(0, 0, clipBounds.width - resolvedX, clipBounds.height); //Right part
                g.drawImage(sub1, 0, resolvedY, null);
                g.drawImage(sub2, resolvedX, resolvedY, null);
            }
        }
    }
}
