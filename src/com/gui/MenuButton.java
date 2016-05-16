package com.gui;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

class MenuButton extends MenuItem implements MouseListener{

    private final ArrayList<ActionListener> listeners = new ArrayList<>();
    private final Font font = new Font("Serif", Font.BOLD, 30);
    private final FontMetrics metrics = getFontMetrics(font);
    private boolean focused;

    public MenuButton(Component parent, String text, Dimension dimension, int xCenterOffset, int yCenterOffset) {
        super(parent, text, dimension, xCenterOffset, yCenterOffset);

        enableInputMethods(true);
        addMouseListener(this);
    }

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        g.setColor(focused ? new Color(128, 128, 128, 128) : new Color(64, 64, 64, 64));
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setColor(Color.black);
        g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);

        g.setColor(getForeground());
        g.setFont(font);

        int x = (getWidth() - metrics.stringWidth(getText())) / 2;
        int y = (getHeight() - metrics.getHeight()) / 2 + metrics.getAscent();

        g.drawString(getText(), x, y);
    }

    public void doClick() {
        notifyListeners();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        notifyListeners();
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

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

    public void addActionListener(ActionListener listener) {
        listeners.add(listener);
    }

    private void notifyListeners() {

        ActionEvent ae = new ActionEvent(this, 1001, "");

        for (ActionListener listener : listeners) listener.actionPerformed(ae);
    }
}
