package com.alientome.game.background;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Layer {

    private final BufferedImage image;
    private final Rectangle optimal;
    private final double xCoefficient;
    private final double yCoefficient;

    private Layer(BufferedImage image, Rectangle optimal, double xCoefficient, double yCoefficient) {
        this.image = image;
        this.optimal = optimal;
        this.xCoefficient = xCoefficient;
        this.yCoefficient = yCoefficient;
    }

    public static Layer of(BufferedImage image, double xCoefficient, double yCoefficient) {

        // Remove as many transparent rows as possible
        Rectangle optimal = BgUtil.findOptimalRect(image);
        BufferedImage optimised = BgUtil.crop(image, optimal);

        return new Layer(optimised, optimal, xCoefficient, yCoefficient);
    }

    public void draw(Graphics g, Rectangle clipBounds, int x, int y) {

        if (xCoefficient == 0 && yCoefficient == 0)
            g.drawImage(image, 0, 0, null); //Fixed background : simply draw image.
        else {

            int resolvedX = (int) (x * xCoefficient) % clipBounds.width; //Scale and wrap around on x axis
            int resolvedY = (int) (y * yCoefficient); //Just scale on y axis

            if (resolvedX == 0) {
                g.drawImage(image, 0, optimal.y + resolvedY, null);
                //No need to wrap around; should fit perfectly on screen
            } else {
                BufferedImage sub1, sub2; //Create sub images to implement wrap around.
                sub1 = image.getSubimage(clipBounds.width - resolvedX, 0, resolvedX, optimal.height); //Left part
                sub2 = image.getSubimage(0, 0, clipBounds.width - resolvedX, optimal.height); //Right part
                g.drawImage(sub1, 0, optimal.y + resolvedY, null);
                g.drawImage(sub2, resolvedX, optimal.y + resolvedY, null);
            }
        }
    }
}
