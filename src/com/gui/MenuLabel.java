package com.gui;

import java.awt.*;

class MenuLabel extends MenuItem {

    public static final int LEFT = 0, CENTER = 1, RIGHT = 2;

    private final int alignment;

    public MenuLabel(String text, Dimension dimension, int alignment, Font font) {
        super(text, dimension, font);

        this.alignment = alignment;
    }

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        FontMetrics metrics = g.getFontMetrics();

        int x;
        int y = (getHeight() - metrics.getHeight()) / 2 + metrics.getAscent();

        switch (alignment) {

            case CENTER:
                x = (getWidth() - metrics.stringWidth(getText())) / 2;
                break;

            case RIGHT:
                x = getWidth() - metrics.stringWidth(getText());
                break;

            default:
                x = 10;
                break;
        }

        g.drawString(getText(), x, y);
    }
}
