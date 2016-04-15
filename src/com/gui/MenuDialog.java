package com.gui;

import com.util.SpringUtilities;

import javax.swing.*;
import java.awt.*;

public class MenuDialog extends JDialog {

    public static final int RESUME = 0;
    public static final int RESET = 1;
    public static final int QUIT = 2;
    private final CardLayout cl = new CardLayout();
    private final String[] names = {"Main", "Options"};
    private int result;

    public MenuDialog(JFrame owner, String title, boolean modal) {
        super(owner, title, modal);

        setSize(250, 250);
        setLocationRelativeTo(null);
        setResizable(false);

        init();
    }

    private void init() {

        getContentPane().setLayout(cl);

        JPanel main = new JPanel(new GridLayout(2, 2, 10, 10));

        JPanel options = new JPanel(new BorderLayout());

        SpringLayout sl = new SpringLayout();

        JPanel keys = new JPanel(sl);
        keys.setFocusable(true);

        keys.setBorder(BorderFactory.createTitledBorder("Key Bindings"));

        keys.setFocusable(true);

        String[] labels = {"Jump : ", "Move Left : ", "Move Right : ", "Fire : ", "Debug : "};

        for (int i = 0; i < labels.length; i++) {

            JLabel label = new JLabel(labels[i], JLabel.TRAILING);
            keys.add(label);
            KeyButton button = new KeyButton(keys, i);
            label.setLabelFor(button);
            keys.add(button);
        }

        SpringUtilities.makeCompactGrid(keys, labels.length, 2, 5, 5, 5, 5);

        JButton buttonBack = new JButton("Done");
        buttonBack.addActionListener(e -> cl.show(getContentPane(), names[0]));

        options.add(keys, BorderLayout.CENTER);
        options.add(buttonBack, BorderLayout.SOUTH);

        JButton buttonOptions = new JButton("Options");
        buttonOptions.addActionListener(e -> {
            cl.show(getContentPane(), names[1]);
            keys.requestFocus();
        });
        JButton buttonResume = new JButton("Resume");
        buttonResume.addActionListener(e -> {
            result = RESUME;
            setVisible(false);
        });
        JButton buttonReset = new JButton("Reset");
        buttonReset.addActionListener(e -> {
            result = RESET;
            setVisible(false);
        });
        JButton buttonQuit = new JButton("Quit");
        buttonQuit.addActionListener(e -> {
            result = QUIT;
            setVisible(false);
        });

        buttonOptions.setFocusable(false);
        buttonResume.setFocusable(false);
        buttonReset.setFocusable(false);
        buttonQuit.setFocusable(false);

        main.add(buttonOptions);
        main.add(buttonResume);
        main.add(buttonReset);
        main.add(buttonQuit);

        getContentPane().add(main, names[0]);
        getContentPane().add(options, names[1]);
    }

    public int showDialog() {
        setVisible(true);
        return result;
    }
}
