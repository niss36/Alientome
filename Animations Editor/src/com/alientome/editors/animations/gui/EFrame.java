package com.alientome.editors.animations.gui;

import com.alientome.editors.animations.SpritesLoader;
import com.alientome.editors.animations.dto.AnimationDTO;
import com.alientome.editors.animations.dto.ClassDTO;
import com.alientome.editors.animations.dto.PackageDTO;
import com.alientome.editors.animations.gui.dialogs.ConfirmDialog;
import com.alientome.editors.animations.gui.dialogs.NewAnimationDialog;
import com.alientome.editors.animations.gui.dialogs.NewClassDialog;
import com.alientome.editors.animations.gui.dialogs.NewPackageDialog;
import com.alientome.editors.animations.gui.panels.AnimationPanel;
import com.alientome.editors.animations.util.AnimationID;
import com.alientome.editors.animations.util.Settings;
import com.alientome.editors.animations.util.TreeView;
import jiconfont.icons.FontAwesome;
import jiconfont.swing.IconFontSwing;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static com.alientome.editors.animations.gui.dialogs.ConfirmDialog.ACCEPT;
import static com.alientome.editors.animations.util.TreeView.ANIMATION;
import static com.alientome.editors.animations.util.Util.parse;

public class EFrame extends JFrame {

    private final Settings settings;
    private final JTree animationsTree;
    private TreePath selected;

    public EFrame(DefaultTreeModel animations, Settings settings) {

        super("Alientome Animations Editor");

        setSize(800, 800);
        setLocationRelativeTo(null);
        setResizable(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        this.settings = settings;

        IconFontSwing.register(FontAwesome.getIconFont());

        animationsTree = new JTree();
        TreeCellRenderer renderer = new CustomTreeCellRenderer(
                IconFontSwing.buildIcon(FontAwesome.FOLDER_OPEN, 16),
                IconFontSwing.buildIcon(FontAwesome.FOLDER, 16),
                IconFontSwing.buildIcon(FontAwesome.FILE_VIDEO_O, 16),
                IconFontSwing.buildIcon(FontAwesome.TIMES, 16, Color.red));
        animationsTree.setCellRenderer(renderer);
        setModel(animations);

        AnimationPanel animationPanel = new AnimationPanel(this);

        JPopupMenu rootPopup = new JPopupMenu();
        JMenuItem menuItemNewPackage = new JMenuItem("New package");

        rootPopup.add(menuItemNewPackage);

        JPopupMenu packagePopup = new JPopupMenu();
        JMenuItem menuItemNewClass = new JMenuItem("New class");
        JMenuItem menuItemDeletePackage = new JMenuItem("Delete");

        packagePopup.add(menuItemNewClass);
        packagePopup.addSeparator();
        packagePopup.add(menuItemDeletePackage);

        JPopupMenu classPopup = new JPopupMenu();
        JMenuItem menuItemNewAnimation = new JMenuItem("New animation");
        JMenuItem menuItemDeleteClass = new JMenuItem("Delete");

        classPopup.add(menuItemNewAnimation);
        classPopup.addSeparator();
        classPopup.add(menuItemDeleteClass);

        JPopupMenu animationPopup = new JPopupMenu();
        JMenuItem menuItemDeleteAnimation = new JMenuItem("Delete");

        animationPopup.add(menuItemDeleteAnimation);

        menuItemNewPackage.addActionListener(e -> {

            PackageDTO info = new NewPackageDialog(this).showDialog();
            if (info != null)
                SpritesLoader.addNewPackage((DefaultTreeModel) animationsTree.getModel(), (DefaultMutableTreeNode) animationsTree.getLastSelectedPathComponent(), info);
        });

        menuItemNewClass.addActionListener(e -> {

            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) animationsTree.getLastSelectedPathComponent();

            ClassDTO info = new NewClassDialog(this, selectedNode).showDialog();
            if (info != null) {
                SpritesLoader.addNewClass((DefaultTreeModel) animationsTree.getModel(), selectedNode, info);

                TreePath path = new TreePath(selectedNode.getPath());

                animationsTree.expandPath(path);
            }
        });

        menuItemDeletePackage.addActionListener(e -> {

            int result = new ConfirmDialog(this, "Confirm deletion", "Delete this package ?").showDialog();

            if (result == ACCEPT)
                SpritesLoader.deletePackage((DefaultTreeModel) animationsTree.getModel(), (DefaultMutableTreeNode) animationsTree.getLastSelectedPathComponent());
        });

        menuItemNewAnimation.addActionListener(e -> {

            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) animationsTree.getLastSelectedPathComponent();

            AnimationDTO info = new NewAnimationDialog(this, selectedNode).showDialog();
            if (info != null) {
                SpritesLoader.addNewAnimation((DefaultTreeModel) animationsTree.getModel(), selectedNode, info);

                TreePath path = new TreePath(selectedNode.getPath());

                animationsTree.expandPath(path);
            }
        });

        menuItemDeleteClass.addActionListener(e -> {

            int result = new ConfirmDialog(this, "Confirm deletion", "Delete this class ?").showDialog();

            if (result == ACCEPT)
                SpritesLoader.deleteClass((DefaultTreeModel) animationsTree.getModel(), (DefaultMutableTreeNode) animationsTree.getLastSelectedPathComponent());
        });

        menuItemDeleteAnimation.addActionListener(e -> {

            int result = new ConfirmDialog(this, "Confirm deletion", "Delete this animation ?").showDialog();

            if (result == ACCEPT) {

                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) animationsTree.getLastSelectedPathComponent();

                DefaultMutableTreeNode closestNode = selectedNode.getPreviousSibling();

                if (closestNode == null)
                    closestNode = selectedNode.getNextSibling();

                TreePath path = closestNode == null ? null : new TreePath(closestNode.getPath());

                SpritesLoader.deleteAnimation((DefaultTreeModel) animationsTree.getModel(), selectedNode);

                if (path == null) {

                    selected = null;
                    animationPanel.setAnimation(null);
                }

                animationsTree.setSelectionPath(path);
            }
        });

        JPopupMenu[] popupMenus = {rootPopup, packagePopup, classPopup, animationPopup};

        MouseAdapter listener = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                if (SwingUtilities.isRightMouseButton(e)) {

                    int row = animationsTree.getRowForLocation(e.getX(), e.getY());
                    TreePath path = animationsTree.getPathForLocation(e.getX(), e.getY());

                    if (row != -1 && path != null) {

                        animationsTree.setSelectionRow(row);

                        TreeView view = (TreeView) ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject();

                        if (view.getType() <= ANIMATION)
                            popupMenus[view.getType()].show(e.getComponent(), e.getX(), e.getY());
                    }
                }
            }
        };

        animationsTree.addMouseListener(listener);

        JComponent treeBar = new JScrollPane(animationsTree);

        animationsTree.addTreeSelectionListener(e -> {
            Object component = animationsTree.getLastSelectedPathComponent();
            if (component != null) {
                TreeView view = (TreeView) ((DefaultMutableTreeNode) component).getUserObject();
                if (view != null && view.getType() == ANIMATION) {
                    selected = e.getPath();
                    AnimationID id = parse(selected);

                    animationPanel.setAnimation(SpritesLoader.getAnimation(id));
                }
            }
        });

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, treeBar, animationPanel);
        splitPane.setOneTouchExpandable(true);

        splitPane.setResizeWeight(0.3);

        setContentPane(splitPane);
    }

    public TreePath getSelected() {
        return selected;
    }

    public void setModel(DefaultTreeModel model) {
        animationsTree.setModel(model);
        animationsTree.expandRow(0);
        selected = null;
    }

    public Settings getSettings() {
        return settings;
    }
}
