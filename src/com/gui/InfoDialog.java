package com.gui;

import javax.swing.*;
import java.awt.*;

public class InfoDialog extends GameDialog {

    public InfoDialog(String... messages) {
        super(true, messages);
    }

    @Override
    JPanel createChoices() {

        JPanel choices = new JPanel();
        GameButton buttonAccept = new GameButton(new Dimension(120, 40), "gui.ok");
        buttonAccept.addActionListener(e -> select(ACCEPT));

        choices.add(buttonAccept);

        return choices;
    }
}
