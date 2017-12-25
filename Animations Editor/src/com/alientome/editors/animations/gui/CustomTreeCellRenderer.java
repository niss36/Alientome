package com.alientome.editors.animations.gui;

import com.alientome.editors.animations.util.TreeView;
import com.alientome.editors.animations.util.Util;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

public class CustomTreeCellRenderer extends DefaultTreeCellRenderer {

    private final Icon folderOpen;
    private final Icon folderClosed;
    private final Icon animation;
    private final Icon animationMissing;

    public CustomTreeCellRenderer(Icon folderOpen, Icon folderClosed, Icon animation, Icon animationMissing) {
        this.folderOpen = folderOpen;
        this.folderClosed = folderClosed;
        this.animation = animation;
        this.animationMissing = animationMissing;
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        JLabel component = (JLabel) super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

        if (value instanceof DefaultMutableTreeNode) {

            DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;

            if (node.getUserObject() instanceof TreeView) {

                TreeView view = (TreeView) node.getUserObject();

                int type = view.getType();

                Icon icon;

                if (type == TreeView.ANIMATION)
                    if (Util.isAnimationMissing(node))
                        icon = animationMissing;
                    else
                        icon = animation;
                else if (expanded)
                    icon = folderOpen;
                else
                    icon = folderClosed;

                component.setIcon(icon);
            }
        }

        return component;
    }
}
