package com.gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

class MenuButton extends MenuItem implements MouseListener {

    private final ArrayList<ActionListener> listeners = new ArrayList<>();
    private boolean focused;

    MenuButton(String text, Dimension dimension) {
        this(text, dimension, null);
    }

    MenuButton(String text, Dimension dimension, Font font) {
        super(text, dimension, font);

        addMouseListener(this);
    }

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        FontMetrics metrics = g.getFontMetrics();

        int bgColor = focused ? 192 : 128;
        int borderRadius = 16;

        g.setColor(new Color(bgColor, bgColor, bgColor, bgColor));
        g.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, borderRadius, borderRadius);

        g.setColor(Color.black);
        g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, borderRadius, borderRadius);

        g.setColor(getForeground());

        int x = (getWidth() - metrics.stringWidth(getText())) / 2;
        int y = (getHeight() - metrics.getHeight()) / 2 + metrics.getAscent();

        g.drawString(getText(), x, y);
    }

    void doClick() {
        notifyListeners();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        notifyListeners();
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        focused = true;
        repaint();
    }

    @Override
    public void mouseExited(MouseEvent e) {
        focused = false;
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
