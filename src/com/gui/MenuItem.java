package com.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

abstract class MenuItem extends JComponent {

    private final Component parent;
    private final int xCenterOffset;
    private final int yTopOffset;
    private String text;

    MenuItem(Component parent, String text, Dimension dimension, int xCenterOffset, int yTopOffset) {

        super();

        this.parent = parent;
        this.text = text;
        this.xCenterOffset = xCenterOffset;
        this.yTopOffset = yTopOffset;

        setPreferredSize(dimension);

        setBounds();

        parent.addComponentListener(new ItemParentListener());
    }

    String getText() {
        return text;
    }

    void setText(String text) {
        this.text = text;
        repaint();
    }

    private void setBounds() {

        Dimension d = getPreferredSize();

        setBounds((parent.getWidth() - getWidth()) / 2 + xCenterOffset,
                yTopOffset,
                d.width, d.height);
    }

    class ItemParentListener implements ComponentListener {

        @Override
        public void componentResized(ComponentEvent e) {
            setBounds();
        }

        @Override
        public void componentMoved(ComponentEvent e) {

        }

        @Override
        public void componentShown(ComponentEvent e) {

        }

        @Override
        public void componentHidden(ComponentEvent e) {

        }
    }
}
