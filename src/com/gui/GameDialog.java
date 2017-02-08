package com.gui;

import com.keybindings.InputManager;

import javax.swing.*;
import java.awt.*;

public abstract class GameDialog extends JDialog {

    static Frame gameFrame;
    static final int ACCEPT = 0, DENY = 1;

    private int selected = -1;

    GameDialog(boolean modal, String... messages) {
        super(gameFrame, modal);

        setSize(480, 130 + 40 * messages.length);
        setLocationRelativeTo(gameFrame);
        setResizable(false);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        content.add(Box.createVerticalGlue());
        for (String message : messages) {
            content.add(Box.createRigidArea(new Dimension(0, 5)));
            content.add(new GameLabel(new Dimension(440, 40), message, null, GameLabel.LEFT));
        }
        content.add(Box.createRigidArea(new Dimension(0, 30)));
        content.add(createChoices());
        content.add(Box.createVerticalGlue());

        setContentPane(content);
    }

    public int showDialog() {

        String previousContext = InputManager.getInstance().getActiveContext();
        InputManager.getInstance().setActiveContext("dialog");
        setVisible(true);
        InputManager.getInstance().setActiveContext(previousContext);
        return selected;
    }

    void select(int option) {
        selected = option;
        setVisible(false);
    }

    abstract JPanel createChoices();
}
