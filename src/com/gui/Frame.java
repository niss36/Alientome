package com.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class Frame extends JFrame implements FocusListener {

    private static final Frame ourInstance = new Frame();
    public final Panel panelGame = new Panel();

    private Frame() {

        setTitle("Alientome");
        setSize(800, 600);
        setExtendedState(getExtendedState() | MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        addFocusListener(this);

        JPanel content = new JPanel();
        content.setLayout(new BorderLayout());
        content.add(panelGame, BorderLayout.CENTER);

        setContentPane(content);
    }

    public static Frame getInstance() {
        return ourInstance;
    }

    @Override
    public void focusGained(FocusEvent e) {
    }

    @Override
    public void focusLost(FocusEvent e) {
        panelGame.game.pause();
    }
}
