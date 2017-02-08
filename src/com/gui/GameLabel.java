package com.gui;

import java.awt.*;

public class GameLabel extends GameTextComponent {

    public static final int LEFT = 0, CENTER = 1, RIGHT = 2;

    private final int alignment;

    public GameLabel(Dimension dimension, String unlocalizedText, Font font, int alignment) {
        super(dimension, unlocalizedText, font);

        this.alignment = alignment;
    }

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        FontMetrics metrics = g.getFontMetrics();

        int stringWidth = metrics.stringWidth(getText()) - g.getFont().getSize() / 10;
        int x;
        int y = (getHeight() - metrics.getHeight()) / 2 + metrics.getAscent();

        switch (alignment) {

            case CENTER:
                x = (getWidth() - stringWidth) / 2;
                break;

            case RIGHT:
                x = getWidth() - stringWidth;
                break;

            default:
                x = 0;
                break;
        }

        if (debugUI) {
            Color c = g.getColor();

            g.setColor(Color.red);
            g.drawLine(0, getHeight() / 2, getWidth(), getHeight() / 2);

            g.drawLine(getWidth() / 2, 0, getWidth() / 2, getHeight());

            g.setColor(Color.cyan);
            g.drawLine(0, y, getWidth(), y);

            g.drawLine(x, 0, x, getHeight());

            g.setColor(Color.green);
            g.drawLine(0, y - metrics.getAscent(), getWidth(), y - metrics.getAscent());

            g.drawLine(x + stringWidth, 0, x + stringWidth, getHeight());

            g.setColor(Color.blue);
            g.drawLine(0, y + metrics.getDescent(), getWidth(), y + metrics.getDescent());

            g.setColor(c);
        }
        g.drawString(getText(), x, y);
    }

}
