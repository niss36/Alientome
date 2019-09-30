package com.alientome.editors.animations.gui.panels;

import com.alientome.core.util.Direction;
import com.alientome.editors.animations.ExtAnimation;
import jiconfont.icons.FontAwesome;
import jiconfont.swing.IconFontSwing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseWheelEvent;

public class AnimationViewControlPanel extends JPanel {

    private final AnimationViewPanel controlled;

    public AnimationViewControlPanel(AnimationViewPanel controlled) {

        super(new BorderLayout());

        this.controlled = controlled;

        JPanel rightControl = new JPanel();

        rightControl.setLayout(new BoxLayout(rightControl, BoxLayout.Y_AXIS));

        JSlider sliderScale = new JSlider(1, 16);
        sliderScale.setPaintTicks(true);
        sliderScale.setOrientation(SwingConstants.VERTICAL);
        sliderScale.setValue(1);
        sliderScale.setMajorTickSpacing(5);
        sliderScale.setMinorTickSpacing(1);
        sliderScale.addChangeListener(e -> controlled.setScale(sliderScale.getValue()));

        JLabel labelZoomPlus = new JLabel(IconFontSwing.buildIcon(FontAwesome.SEARCH_PLUS, 20));
        JLabel labelZoomMinus = new JLabel(IconFontSwing.buildIcon(FontAwesome.SEARCH_MINUS, 20));

        rightControl.add(Box.createVerticalGlue());
        rightControl.add(labelZoomPlus);
        rightControl.add(sliderScale);
        rightControl.add(labelZoomMinus);
        rightControl.add(Box.createVerticalGlue());

        JPanel bottomControl = new JPanel();

        PlayControlPanel panel = new PlayControlPanel(controlled);

        JRadioButton radioLeft = new JRadioButton("Left");
        radioLeft.addActionListener(e -> controlled.setFacing(Direction.LEFT));
        radioLeft.setFocusable(false);
        JRadioButton radioRight = new JRadioButton("Right");
        radioRight.addActionListener(e -> controlled.setFacing(Direction.RIGHT));
        radioRight.setFocusable(false);

        ButtonGroup facingSelect = new ButtonGroup();
        facingSelect.add(radioLeft);
        facingSelect.add(radioRight);
        radioLeft.setSelected(true);

        JCheckBox checkBoxOutLine = new JCheckBox("Draw Outline");
        checkBoxOutLine.addActionListener(e -> controlled.setDrawOutline(checkBoxOutLine.isSelected()));

        bottomControl.add(panel);
        bottomControl.add(radioLeft);
        bottomControl.add(radioRight);
        bottomControl.add(checkBoxOutLine);

        add(controlled, BorderLayout.CENTER);
        add(rightControl, BorderLayout.EAST);
        add(bottomControl, BorderLayout.SOUTH);

        Toolkit.getDefaultToolkit().addAWTEventListener(e -> {

            if (e instanceof MouseWheelEvent) {
                MouseWheelEvent mouseWheelEvent = (MouseWheelEvent) e;

                if (e.getSource() instanceof JSplitPane) {
                    sliderScale.setValue(sliderScale.getValue() - mouseWheelEvent.getWheelRotation());
                    mouseWheelEvent.consume();
                }
            }
        }, AWTEvent.MOUSE_WHEEL_EVENT_MASK);
    }

    public void setAnimation(ExtAnimation animation) {
        controlled.setAnimation(animation);
    }

    public ExtAnimation getCurrentAnimation() {
        return controlled.getCurrentAnimation();
    }
}
