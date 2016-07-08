package com.gui;

import com.util.Config;
import com.util.listeners.ConfigListener;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class MenuSwitchButton extends MenuButton implements ActionListener, ConfigListener {

    private final int index;
    private boolean currentValue;

    public MenuSwitchButton(Dimension dimension, int index) {
        super("", dimension);

        this.index = index;

        setCurrentValue(Config.getInstance().getOption(index));

        addActionListener(this);
    }

    private void setCurrentValue(boolean value) {
        currentValue = value;
        setText(value ? "YES" : "NO");
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        setCurrentValue(!currentValue);

        Config.getInstance().setOption(index, currentValue);
    }

    @Override
    public void configReset() {
        setCurrentValue(Config.getInstance().getOption(index));
    }

    @Override
    public void configKeysReset() {
    }
}
