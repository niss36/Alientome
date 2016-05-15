package com.gui;

import com.game.Game;
import com.game.Game.State;

import javax.swing.*;
import java.awt.*;

public class MenuLabel extends JComponent {

    private final String text;

    private final Game game;

    private final State requiredState;

    private final Font font = new Font("Serif", Font.BOLD, 30);
    private final FontMetrics metrics = getFontMetrics(font);

    public MenuLabel(String text, Game game, Rectangle rec, State requiredState) {

        this.text = text;
        this.game = game;
        this.requiredState = requiredState;

        setBounds(rec);
    }

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        if(game.state != requiredState) return;

        g.setColor(getForeground());
        g.setFont(font);

        int x = (getWidth() - metrics.stringWidth(text)) / 2;
        int y = (getHeight() - metrics.getHeight()) / 2 + metrics.getAscent();

        g.drawString(text, x, y);
    }
}
