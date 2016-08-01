package com.gui;

import javax.swing.*;
import java.awt.*;

public class ConfirmDialog extends JDialog {

    public static final int ACCEPT = 0, DENY = 1;

    private int selected = -1;

    public ConfirmDialog(java.awt.Frame owner, String message) {
        super(owner, true);

        setSize(480, 200);
        setLocationRelativeTo(owner);
        setResizable(false);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        JPanel choices = new JPanel();
        choices.setLayout(new BoxLayout(choices, BoxLayout.X_AXIS));

        MenuButton buttonAccept = new MenuButton("Yes", new Dimension(210, 40));
        buttonAccept.addActionListener(e -> {
            selected = ACCEPT;
            setVisible(false);
        });
        MenuButton buttonDeny = new MenuButton("Cancel", new Dimension(210, 40));
        buttonDeny.addActionListener(e -> {
            selected = DENY;
            setVisible(false);
        });

        choices.add(Box.createHorizontalGlue());
        choices.add(buttonAccept);
        choices.add(Box.createRigidArea(new Dimension(20, 0)));
        choices.add(buttonDeny);
        choices.add(Box.createHorizontalGlue());

        content.add(Box.createVerticalGlue());
        content.add(new MenuLabel(message, new Dimension(440, 40), MenuLabel.LEFT, null));
        content.add(Box.createRigidArea(new Dimension(0, 30)));
        content.add(choices);
        content.add(Box.createVerticalGlue());

        setContentPane(content);
    }

    public int showDialog() {

        setVisible(true);
        return selected;
    }
}
