package com.alientome.editors.animations.gui.dialogs;

import com.alientome.editors.animations.gui.EFrame;

import javax.swing.*;

import static com.alientome.editors.animations.util.Util.createBoxLayoutPanel;
import static com.alientome.editors.animations.util.Util.setFontSize;
import static javax.swing.BoxLayout.X_AXIS;
import static javax.swing.BoxLayout.Y_AXIS;

public class InfoDialog extends JDialog {

    public InfoDialog(EFrame frame, String title, String message) {

        super(frame, title, true);

        setSize(300, 140);
        setLocationRelativeTo(frame);
        setResizable(false);

        float fontSize = 15f;

        JLabel label = setFontSize(new JLabel(message, SwingConstants.CENTER), fontSize);

        JButton button = setFontSize(new JButton("OK"), fontSize);
        button.addActionListener(e -> setVisible(false));

        JPanel labelPanel = createBoxLayoutPanel(X_AXIS, Box.createHorizontalGlue(), label, Box.createHorizontalGlue());

        JPanel buttonPanel = createBoxLayoutPanel(X_AXIS, Box.createHorizontalGlue(), button, Box.createHorizontalGlue());

        JPanel content = createBoxLayoutPanel(Y_AXIS, Box.createVerticalStrut(20), labelPanel, Box.createVerticalStrut(20), buttonPanel);

        setContentPane(content);
    }

    public void showDialog() {
        setVisible(true);
    }
}
