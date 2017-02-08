package com.gui;

import com.util.listeners.BooleanValueListener;
import com.util.visual.SpritesLoader;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class GameCheckbox extends GameButton {

    private static final BufferedImage scarabOn = SpritesLoader.readImage("UI/scarabOn");
    private static final BufferedImage scarabOff = SpritesLoader.readImage("UI/scarabOff");

    private final List<BooleanValueListener> listeners = new ArrayList<>();
    private boolean checked = false;

    public GameCheckbox(Dimension d) {
        super(d, "");

        addActionListener(e -> {
            setChecked(!checked);
            for (BooleanValueListener listener : listeners)
                listener.valueChanged(checked);
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        paintButtonBase(g);

        int x;
        int y;
        int width;
        int height;

        int scaleFactor = getWidth() / scarabOn.getWidth();

        if (scaleFactor > 1) {
            width = scarabOn.getWidth() * scaleFactor;
            height = scarabOn.getHeight() * scaleFactor;

        } else {
            width = scarabOn.getWidth();
            height = scarabOn.getHeight();
        }

        x = getWidth() / 2 - width / 2;
        y = getHeight() / 2 - height / 2;

        g.drawImage(checked ? scarabOn : scarabOff, x, y, width, height, this);
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
        repaint();
    }

    public void addCheckboxListener(BooleanValueListener listener) {
        listeners.add(listener);
    }
}
