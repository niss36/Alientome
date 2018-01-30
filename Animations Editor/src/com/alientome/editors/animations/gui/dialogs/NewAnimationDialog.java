package com.alientome.editors.animations.gui.dialogs;

import com.alientome.editors.animations.dto.AnimationDTO;
import com.alientome.editors.animations.gui.EFrame;
import com.alientome.editors.animations.util.TreeView;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;

import static com.alientome.editors.animations.util.Util.createBoxLayoutPanel;
import static com.alientome.editors.animations.util.Util.createRigidArea;
import static javax.swing.Box.createVerticalStrut;
import static javax.swing.BoxLayout.X_AXIS;
import static javax.swing.BoxLayout.Y_AXIS;

public class NewAnimationDialog extends JDialog {

    private AnimationDTO info;

    public NewAnimationDialog(EFrame frame, DefaultMutableTreeNode selectedNode) {

        super(frame, "New Animation", true);

        setSize(300, 250);
        setLocationRelativeTo(frame);
        setResizable(false);

        JLabel animationNameLabel = new JLabel("Animation name : ");
        JLabel lengthLabel = new JLabel("Animation length : ");
        JLabel delayLabel = new JLabel("Delay : ");
        JLabel loopLabel = new JLabel("Loop : ");
        JLabel dimensionLabel = new JLabel("Dimension : ");
        JLabel xLabel = new JLabel("x");
        JLabel scaleLabel = new JLabel("Scale : ");

        JTextField animationNameTextField = new JTextField();
        JTextField lengthTextField = new JTextField();
        JTextField delayTextField = new JTextField();
        JCheckBox loopCheckbox = new JCheckBox();
        loopCheckbox.setSelected(true);
        JTextField dimensionWidthTextField = new JTextField();
        JTextField dimensionHeightTextField = new JTextField();
        JTextField scaleTextField = new JTextField();

        JButton addButton = new JButton("Add");
        addButton.addActionListener(e -> {

            DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) selectedNode.getParent();
            TreeView selectedView = (TreeView) selectedNode.getUserObject();
            TreeView parentView = (TreeView) parentNode.getUserObject();

            info = new AnimationDTO((String) parentView.getValue(),
                    (String) selectedView.getValue(),
                    animationNameTextField.getText(),
                    Integer.parseInt(lengthTextField.getText()),
                    Integer.parseInt(delayTextField.getText()),
                    loopCheckbox.isSelected(),
                    new Dimension(Integer.parseInt(dimensionWidthTextField.getText()), Integer.parseInt(dimensionHeightTextField.getText())),
                    Integer.parseInt(scaleTextField.getText()));
            setVisible(false);
        });

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> {

            info = null;
            setVisible(false);
        });

        JPanel animationName = createBoxLayoutPanel(X_AXIS, animationNameLabel, animationNameTextField);

        JPanel length = createBoxLayoutPanel(X_AXIS, lengthLabel, lengthTextField);

        JPanel delay = createBoxLayoutPanel(X_AXIS, delayLabel, delayTextField);

        JPanel loop = createBoxLayoutPanel(X_AXIS, loopLabel, Box.createHorizontalGlue(), loopCheckbox, Box.createHorizontalGlue());

        JPanel dimension = createBoxLayoutPanel(X_AXIS, dimensionLabel, dimensionWidthTextField, xLabel, dimensionHeightTextField);

        JPanel scale = createBoxLayoutPanel(X_AXIS, scaleLabel, scaleTextField);

        JPanel buttons = createBoxLayoutPanel(X_AXIS,
                Box.createHorizontalGlue(),
                addButton,
                createRigidArea(20, 0),
                cancelButton,
                Box.createHorizontalGlue());

        JPanel content = createBoxLayoutPanel(Y_AXIS,
                createVerticalStrut(10),
                animationName,
                createVerticalStrut(10),
                length,
                createVerticalStrut(10),
                delay,
                createVerticalStrut(10),
                loop,
                createVerticalStrut(10),
                dimension,
                createVerticalStrut(10),
                scale,
                createVerticalStrut(10),
                buttons);

        setContentPane(content);
    }

    public AnimationDTO showDialog() {
        setVisible(true);
        return info;
    }
}
