package com.gui;

import javax.swing.*;
import java.awt.*;

abstract class MenuItem extends JComponent {

    Font font = new Font("Serif", Font.BOLD, 30);
    FontMetrics metrics = getFontMetrics(font);
    private String text;

    MenuItem(String text, Dimension dimension, Font font) {

        super();

        setItemFont(font);

        this.text = text;

        setPreferredSize(dimension);
        setMinimumSize(dimension);
        setMaximumSize(dimension);
    }

    String getText() {
        return text;
    }

    void setText(String text) {
        this.text = text;
        repaint();
    }

    void setItemFont(Font font) {
        if (font == null) return;
        this.font = font;
        metrics = getFontMetrics(font);
    }
}
