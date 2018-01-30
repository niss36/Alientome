package com.alientome.editors.animations.gui.dialogs;

import com.alientome.editors.animations.dto.ClassDTO;
import com.alientome.editors.animations.gui.EFrame;
import com.alientome.editors.animations.util.TreeView;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;

import static com.alientome.editors.animations.util.Util.createBoxLayoutPanel;
import static com.alientome.editors.animations.util.Util.createRigidArea;
import static javax.swing.BoxLayout.X_AXIS;
import static javax.swing.BoxLayout.Y_AXIS;

public class NewClassDialog extends JDialog {

    private ClassDTO info;

    public NewClassDialog(EFrame frame, DefaultMutableTreeNode selectedNode) {

        super(frame, "New Class", true);

        setSize(300, 150);
        setLocationRelativeTo(frame);
        setResizable(false);

        JLabel classNameLabel = new JLabel("Class name : ");
        JLabel classDirectoryLabel = new JLabel("Class sub-directory : ");

        JTextField classNameTextField = new JTextField();
        JTextField classDirectoryTextField = new JTextField();

        JButton addButton = new JButton("Add");

        addButton.addActionListener(e -> {

            TreeView selectedView = (TreeView) selectedNode.getUserObject();

            info = new ClassDTO((String) selectedView.getValue(), classNameTextField.getText(), classDirectoryTextField.getText());
            setVisible(false);
        });

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> {

            info = null;
            setVisible(false);
        });

        JPanel className = createBoxLayoutPanel(X_AXIS, classNameLabel, classNameTextField);

        JPanel classDirectory = createBoxLayoutPanel(X_AXIS, classDirectoryLabel, classDirectoryTextField);

        JPanel buttons = createBoxLayoutPanel(X_AXIS,
                Box.createHorizontalGlue(),
                addButton,
                createRigidArea(20, 0),
                cancelButton,
                Box.createHorizontalGlue());

        JPanel content = createBoxLayoutPanel(Y_AXIS,
                Box.createVerticalStrut(10),
                className,
                Box.createVerticalStrut(20),
                classDirectory,
                Box.createVerticalStrut(20),
                buttons);

        setContentPane(content);
    }

    public ClassDTO showDialog() {

        setVisible(true);
        return info;
    }
}
