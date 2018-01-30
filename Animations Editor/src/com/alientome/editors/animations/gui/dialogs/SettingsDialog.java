package com.alientome.editors.animations.gui.dialogs;

import com.alientome.editors.animations.gui.EFrame;
import com.alientome.editors.animations.util.Settings;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;

import static com.alientome.core.util.Util.getParentFile;
import static com.alientome.editors.animations.util.Util.createBoxLayoutPanel;
import static com.alientome.editors.animations.util.Util.createRigidArea;
import static javax.swing.Box.createVerticalStrut;
import static javax.swing.BoxLayout.X_AXIS;
import static javax.swing.BoxLayout.Y_AXIS;
import static javax.swing.JFileChooser.APPROVE_OPTION;

public class SettingsDialog extends JDialog {

    public SettingsDialog(EFrame frame) {

        super(frame, "Settings", true);

        Settings settings = frame.getSettings();

        setSize(400, 280);
        setLocationRelativeTo(frame);
        setResizable(false);

        String animationsXMLPath = settings.getString("animationsXMLPath");
        String spritesRootPath = settings.getString("spritesRootPath");
        String externalEditorPath = settings.getString("externalEditorPath");

        JTextField animationsFileTextField = new JTextField(animationsXMLPath);
        animationsFileTextField.setEditable(false);
        JTextField spritesRootTextField = new JTextField(spritesRootPath);
        spritesRootTextField.setEditable(false);
        JTextField editorTextField = new JTextField(externalEditorPath);
        editorTextField.setEditable(false);

        JButton animationsFileChooserButton = new JButton("...");
        JButton spritesRootChooserButton = new JButton("...");
        JButton editorChooserButton = new JButton("...");
        JCheckBox sequentialCheckbox = new JCheckBox("Open Images Sequentially");
        sequentialCheckbox.setSelected(settings.getBoolean("openSequential", false));

        JButton doneButton = new JButton("Done");
        doneButton.addActionListener(e -> setVisible(false));

        File animationsFileParent = getParentFile(animationsXMLPath);
        File spritesRootParent = getParentFile(spritesRootPath);
        File editorParent = getParentFile(externalEditorPath);

        JFileChooser animationsFileChooser = new JFileChooser(animationsFileParent);
        JFileChooser spritesRootChooser = new JFileChooser(spritesRootParent);
        JFileChooser editorChooser = new JFileChooser(editorParent);

        animationsFileChooser.setFileFilter(new FileNameExtensionFilter("XML Animations Files", "xml"));

        spritesRootChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        animationsFileChooserButton.addActionListener(e -> {

            int result = animationsFileChooser.showOpenDialog(SettingsDialog.this);

            if (result == APPROVE_OPTION) {

                File file = animationsFileChooser.getSelectedFile();

                String path = file.getAbsolutePath();

                settings.setString("animationsXMLPath", path);

                animationsFileTextField.setText(path);
            }
        });

        spritesRootChooserButton.addActionListener(e -> {

            int result = spritesRootChooser.showOpenDialog(SettingsDialog.this);

            if (result == APPROVE_OPTION) {

                File file = spritesRootChooser.getSelectedFile();

                String path = file.getAbsolutePath();

                settings.setString("spritesRootPath", path);

                spritesRootTextField.setText(path);
            }
        });

        editorChooserButton.addActionListener(e -> {

            int result = editorChooser.showOpenDialog(SettingsDialog.this);

            if (result == APPROVE_OPTION) {

                File file = editorChooser.getSelectedFile();

                String path = file.getAbsolutePath();

                settings.setString("externalEditorPath", path);

                editorTextField.setText(path);
            }
        });

        sequentialCheckbox.addActionListener(e -> {

            boolean checked = sequentialCheckbox.isSelected();

            settings.setString("openSequential", String.valueOf(checked));
        });

        JPanel animationsFilePanel = createBoxLayoutPanel(X_AXIS, animationsFileTextField, animationsFileChooserButton);
        animationsFilePanel.setBorder(BorderFactory.createTitledBorder("Animations XML File"));

        JPanel spritesRootPanel = createBoxLayoutPanel(X_AXIS, spritesRootTextField, spritesRootChooserButton);
        spritesRootPanel.setBorder(BorderFactory.createTitledBorder("Sprites Root Directory"));

        JPanel editorPathPanel = createBoxLayoutPanel(X_AXIS, editorTextField, editorChooserButton);
        JPanel sequentialPanel = createBoxLayoutPanel(X_AXIS, Box.createHorizontalGlue(), sequentialCheckbox, Box.createHorizontalGlue());

        JPanel externalEditorPanel = createBoxLayoutPanel(Y_AXIS, editorPathPanel, createRigidArea(0, 10), sequentialPanel);
        externalEditorPanel.setBorder(BorderFactory.createTitledBorder("External Editor"));

        JPanel button = createBoxLayoutPanel(X_AXIS, Box.createHorizontalGlue(), doneButton, Box.createHorizontalGlue());

        JPanel content = createBoxLayoutPanel(Y_AXIS,
                createVerticalStrut(10),
                animationsFilePanel,
                createVerticalStrut(10),
                spritesRootPanel,
                createVerticalStrut(10),
                externalEditorPanel,
                createVerticalStrut(10),
                button,
                createVerticalStrut(5));

        setContentPane(content);
    }

    public void showDialog() {

        setVisible(true);
    }
}
