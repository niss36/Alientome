package com.gui;

import java.awt.*;

class MenuLabel extends MenuItem {

    public static final int LEFT = 0, CENTER = 1, RIGHT = 2;

    private final int alignment;

    private final Font font = new Font("Serif", Font.BOLD, 30);
    private final FontMetrics metrics = getFontMetrics(font);

    public MenuLabel(Component parent, String text, Dimension dimension, int xCenterOffset, int yCenterOffset, int alignment) {

        super(parent, text, dimension, xCenterOffset, yCenterOffset);

        this.alignment = alignment;
    }

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        g.setColor(getForeground());
        g.setFont(font);

        int x;
        int y = (getHeight() - metrics.getHeight()) / 2 + metrics.getAscent();

        switch (alignment) {

            case CENTER:
                x = (getWidth() - metrics.stringWidth(getText())) / 2;
                break;

            case RIGHT:
                x = getWidth() - metrics.stringWidth(getText());
                break;

            case LEFT:

            default:
                x = 10;
                break;
        }

        g.drawString(getText(), x, y);
    }
}
