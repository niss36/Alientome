package com.alientome.editors.animations.gui.dialogs;

import com.alientome.editors.animations.dto.PackageDTO;
import com.alientome.editors.animations.gui.EFrame;

import javax.swing.*;

import static com.alientome.editors.animations.util.Util.createBoxLayoutPanel;
import static com.alientome.editors.animations.util.Util.createRigidArea;
import static javax.swing.BoxLayout.X_AXIS;
import static javax.swing.BoxLayout.Y_AXIS;

public class NewPackageDialog extends JDialog {

    private PackageDTO info;

    public NewPackageDialog(EFrame frame) {

        super(frame, "New Package", true);

        setSize(300, 150);
        setLocationRelativeTo(frame);
        setResizable(false);

        JLabel packageNameLabel = new JLabel("Package name : ");
        JLabel packageDirectoryLabel = new JLabel("Package directory : ");

        JTextField packageNameTextField = new JTextField();
        JTextField packageDirectoryTextField = new JTextField();

        JButton addButton = new JButton("Add");
        addButton.addActionListener(e -> {

            info = new PackageDTO(packageNameTextField.getText(), packageDirectoryTextField.getText());
            setVisible(false);
        });

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> {

            info = null;
            setVisible(false);
        });

        JPanel packageName = createBoxLayoutPanel(X_AXIS, packageNameLabel, packageNameTextField);

        JPanel packageDirectory = createBoxLayoutPanel(X_AXIS, packageDirectoryLabel, packageDirectoryTextField);

        JPanel buttons = createBoxLayoutPanel(X_AXIS,
                Box.createHorizontalGlue(),
                addButton,
                createRigidArea(20, 0),
                cancelButton,
                Box.createHorizontalGlue());

        JPanel content = createBoxLayoutPanel(Y_AXIS,
                Box.createVerticalStrut(10),
                packageName,
                Box.createVerticalStrut(20),
                packageDirectory,
                Box.createVerticalStrut(20),
                buttons);

        setContentPane(content);
    }

    public PackageDTO showDialog() {

        setVisible(true);
        return info;
    }
}
