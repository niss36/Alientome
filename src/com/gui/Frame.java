package com.gui;

import javax.swing.*;
import java.awt.*;

public class Frame extends JFrame {

    private static final Frame ourInstance = new Frame();
    public final Panel game = new Panel();

    private Frame() {

        setTitle("Alientome");
        setExtendedState(getExtendedState() | MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JPanel content = new JPanel();
        content.setLayout(new BorderLayout());
        content.add(game, BorderLayout.CENTER);

        setContentPane(content);
    }

    public static Frame getInstance() {
        return ourInstance;
    }
}
