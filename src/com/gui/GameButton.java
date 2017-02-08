package com.gui;

import com.util.visual.SpritesLoader;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

class GameButton extends GameTextComponent implements MouseListener {

    private static final Color border1 = new Color(193, 132, 0);
    private static final Color border2 = new Color(229, 156, 0);
    private static final Color background = new Color(226, 170, 59);

    private static final Color border2Hovered = new Color(226, 169, 54);
    private static final Color backgroundHovered = new Color(224, 184, 109);

    private static final Color backgroundPressed = new Color(221, 197, 150);

    private static final BufferedImage corner = SpritesLoader.readImage("UI/buttonCorner");
    private static final BufferedImage middle = SpritesLoader.readImage("UI/buttonMiddle");

    private final List<ActionListener> listeners = new ArrayList<>();
    private boolean hovered = false;
    private boolean pressed = false;

    GameButton(Dimension dimension, String unlocalizedText) {
        this(dimension, unlocalizedText, null);
    }

    GameButton(Dimension dimension, String unlocalizedText, Font font) {
        super(dimension, unlocalizedText, font);

        addMouseListener(this);
    }

    @Override
    protected void paintComponent(Graphics g) {

        FontMetrics metrics = g.getFontMetrics();

        Color c = g.getColor();

        paintButtonBase(g);

        int stringWidth = metrics.stringWidth(getText()) - g.getFont().getSize() / 10;
        int x = (getWidth() - stringWidth) / 2;
        int y = (getHeight() - metrics.getHeight()) / 2 + metrics.getAscent(); //Font baseline

        g.setColor(c);
        g.drawString(getText(), x, y);

        if (debugUI) {
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
        }
    }

    void paintButtonBase(Graphics g) {

        super.paintComponent(g);

        int width = getWidth() - 1;
        int height = getHeight() - 1;

        g.setColor(border1);
        g.drawRoundRect(0, 0, width, height, 6, 6);
        g.drawRect(1, 1, width - 2, height - 2);
        g.setColor(hovered ? border2Hovered : border2);
        g.drawRect(2, 2, width - 4, height - 4);
        g.drawRect(3, 3, width - 6, height - 6);
        g.setColor(pressed ? backgroundPressed : hovered ? backgroundHovered : background);
        g.fillRect(4, 4, width - 7, height - 7);

        g.drawImage(corner, 2, 2, this);
        g.drawImage(corner, width - 1, 2, -corner.getWidth(), corner.getHeight(), this);
        g.drawImage(corner, 2, height - 1, corner.getWidth(), -corner.getHeight(), this);
        g.drawImage(corner, width - 1, height - 1, -corner.getWidth(), -corner.getHeight(), this);

        g.drawImage(middle, getWidth() / 2 - 4, 2, this);
        g.drawImage(middle, getWidth() / 2 - 4, height - 1, middle.getWidth(), -middle.getHeight(), this);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        pressed = true;
        repaint();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        pressed = false;
        repaint();
        if (hovered) notifyListeners();
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        hovered = true;
        repaint();
    }

    @Override
    public void mouseExited(MouseEvent e) {
        hovered = false;
        repaint();
    }

    void addActionListener(ActionListener listener) {
        listeners.add(listener);
    }

    private void notifyListeners() {

        if (!isEnabled()) return;

        ActionEvent ae = new ActionEvent(this, 1001, "");

        for (ActionListener listener : listeners) listener.actionPerformed(ae);
    }
}
