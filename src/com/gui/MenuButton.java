package com.gui;

import com.game.Game;
import com.game.Game.State;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

public class MenuButton extends JComponent implements MouseListener {

    private final String text;

    private final Game game;

    private final State requiredState;

    private boolean focused;

    private final ArrayList<ActionListener> listeners = new ArrayList<>();

    private final Font font = new Font("Serif", Font.BOLD, 30);
    private final FontMetrics metrics = getFontMetrics(font);

    public MenuButton(String text, Game game, Rectangle rec, State requiredState) {
        super();

        this.text = text;
        this.game = game;
        this.requiredState = requiredState;

        enableInputMethods(true);
        addMouseListener(this);

        setBounds(rec);
    }

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        if(game.state != requiredState) {
            focused = false;
            return;
        }

        g.setColor(focused ? new Color(128, 128, 128, 128) : new Color(64, 64, 64, 64));
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setColor(Color.black);
        g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);

        g.setColor(getForeground());
        g.setFont(font);

        int x = (getWidth() - metrics.stringWidth(text)) / 2;
        int y = (getHeight() - metrics.getHeight()) / 2 + metrics.getAscent();

        g.drawString(text, x, y);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if(game.state == requiredState) notifyListeners();
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {
        if(game.state == requiredState) {
            focused = true;
            repaint();
        }
    }

    @Override
    public void mouseExited(MouseEvent e) {
        if(game.state == requiredState) {
            focused = false;
            repaint();
        }
    }

    public void addActionListener(ActionListener listener) {
        listeners.add(listener);
    }

    private void notifyListeners() {

        ActionEvent ae = new ActionEvent(this, 1001, "");

        for(ActionListener listener : listeners) listener.actionPerformed(ae);
    }
}
