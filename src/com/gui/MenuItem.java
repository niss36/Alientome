package com.gui;

import javax.swing.*;
import java.awt.*;

abstract class MenuItem extends JComponent {

    private String text;

    MenuItem(String text, Dimension dimension, Font font) {

        super();

        if (font != null) setFont(font);
        else setFont(new Font("Serif", Font.BOLD, 30));

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
}
