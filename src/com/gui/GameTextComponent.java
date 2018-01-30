package com.gui;

import java.awt.*;

public abstract class GameTextComponent extends GameComponent {

    private String text;

    GameTextComponent(Dimension dimension, String unlocalizedText, Font font) {

        text = unlocalizedText;

        if (font != null) setFont(font);

        setDimension(dimension);
    }

    String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
        repaint();
    }
}
