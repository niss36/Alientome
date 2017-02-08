package com.gui;

import com.util.GameFont;
import com.util.I18N;

import java.awt.*;

public abstract class GameTextComponent extends GameComponent {

    private static final Font defaultFont = GameFont.get(3);

    private String text;

    GameTextComponent(Dimension dimension, String unlocalizedText, Font font) {

        if (unlocalizedText.equals(""))
            text = "";
        else {
            text = I18N.getString(unlocalizedText);
            I18N.addLangChangedListener(() -> setText(I18N.getString(unlocalizedText)));
        }

        if (font != null) setFont(font);
        else setFont(defaultFont);

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
