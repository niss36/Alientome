package com.alientome.editors.animations.gui.dialogs;

import com.alientome.editors.animations.gui.EFrame;

import javax.swing.*;

import static com.alientome.editors.animations.util.Util.*;
import static javax.swing.BoxLayout.X_AXIS;
import static javax.swing.BoxLayout.Y_AXIS;

public class ConfirmDialog extends JDialog {

    public static final int ACCEPT = 0, DENY = 1;

    private int selected = -1;

    public ConfirmDialog(EFrame frame, String title, String message) {

        super(frame, title, true);

        setSize(300, 140);
        setLocationRelativeTo(frame);
        setResizable(false);

        float fontSize = 15f;

        JLabel label = setFontSize(new JLabel(message, SwingConstants.CENTER), fontSize);

        JButton buttonAccept = setFontSize(new JButton("Yes"), fontSize);
        buttonAccept.addActionListener(e -> {
            selected = ACCEPT;
            setVisible(false);
        });

        JButton buttonDeny = setFontSize(new JButton("No"), fontSize);
        buttonDeny.addActionListener(e -> {
            selected = DENY;
            setVisible(false);
        });

        JPanel buttons = createBoxLayoutPanel(X_AXIS,
                Box.createHorizontalGlue(),
                buttonAccept,
                createRigidArea(20, 0),
                buttonDeny,
                Box.createHorizontalGlue());

        JPanel labelPanel = createBoxLayoutPanel(X_AXIS, Box.createHorizontalGlue(), label, Box.createHorizontalGlue());

        JPanel content = createBoxLayoutPanel(Y_AXIS, Box.createVerticalStrut(20), labelPanel, Box.createVerticalStrut(20), buttons);

        setContentPane(content);
    }

    public int showDialog() {
        setVisible(true);
        return selected;
    }
}
