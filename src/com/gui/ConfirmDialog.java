package com.gui;

import javax.swing.*;
import java.awt.*;

public class ConfirmDialog extends GameDialog {

    public ConfirmDialog(String... messages) {
        super(true, messages);
    }

    @Override
    JPanel createChoices() {

        JPanel choices = new JPanel();
        choices.setLayout(new BoxLayout(choices, BoxLayout.X_AXIS));

        GameButton buttonAccept = new GameButton(new Dimension(210, 40), "gui.yes");
        buttonAccept.addActionListener(e -> select(ACCEPT));

        GameButton buttonDeny = new GameButton(new Dimension(210, 40), "gui.cancel");
        buttonDeny.addActionListener(e -> select(DENY));

        choices.add(Box.createHorizontalGlue());
        choices.add(buttonAccept);
        choices.add(Box.createRigidArea(new Dimension(20, 0)));
        choices.add(buttonDeny);
        choices.add(Box.createHorizontalGlue());

        return choices;
    }
}
