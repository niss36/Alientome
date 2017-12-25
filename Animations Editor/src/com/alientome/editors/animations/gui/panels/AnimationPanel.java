package com.alientome.editors.animations.gui.panels;

import com.alientome.editors.animations.Animation;
import com.alientome.editors.animations.SpritesLoader;
import com.alientome.editors.animations.gui.EFrame;
import com.alientome.editors.animations.gui.dialogs.InfoDialog;
import com.alientome.editors.animations.gui.dialogs.SettingsDialog;
import com.alientome.editors.animations.io.AnimationWriter;
import com.alientome.editors.animations.util.Settings;
import com.alientome.editors.animations.util.TextAlign;
import jiconfont.icons.FontAwesome;
import jiconfont.swing.IconFontSwing;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static com.alientome.editors.animations.util.Util.drawString;
import static com.alientome.editors.animations.util.Util.promptChooseAnimationsXML;

public class AnimationPanel extends JPanel {

    private final CardLayout cl = new CardLayout();
    private final String[] choices = {"NO_ANIMATION", "VIEW"/*, "INFO", "EDIT"*/};
    private final JPanel cards = new JPanel(cl);
    private final AnimationViewControlPanel viewControlPanel;
    private final JMenu menuEdit;
    private File[] cache;
    private int selected = 0;

    public AnimationPanel(EFrame frame) {

        super(new BorderLayout());

        viewControlPanel = new AnimationViewControlPanel(new AnimationViewPanel());

        JMenuBar menu = new JMenuBar();

        MenuListener paintFix = new MenuListener() {
            @Override
            public void menuSelected(MenuEvent e) {
            }

            @Override
            public void menuDeselected(MenuEvent e) {
                cards.repaint();
            }

            @Override
            public void menuCanceled(MenuEvent e) {
            }
        };

        JMenu menuFile = new JMenu("File");
        menuFile.addMenuListener(paintFix);
        menuFile.setMnemonic(KeyEvent.VK_F);

        JMenuItem menuItemOpen = new JMenuItem("Open...", IconFontSwing.buildIcon(FontAwesome.FOLDER_OPEN_O, 12));

        JMenuItem menuItemReloadAll = new JMenuItem("Reload", IconFontSwing.buildIcon(FontAwesome.REFRESH, 12));
        menuItemReloadAll.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, KeyEvent.CTRL_DOWN_MASK));

        JMenuItem menuItemSettings = new JMenuItem("Settings...", IconFontSwing.buildIcon(FontAwesome.COG, 12));

        menuFile.add(menuItemOpen);
        menuFile.addSeparator();
        menuFile.add(menuItemReloadAll);
        menuFile.addSeparator();
        menuFile.add(menuItemSettings);

        menuEdit = new JMenu("Edit");
        menuEdit.addMenuListener(paintFix);
        menuEdit.setMnemonic(KeyEvent.VK_E);

        JMenuItem menuItemEditCurrentFrame = new JMenuItem("Current Frame", IconFontSwing.buildIcon(FontAwesome.EXTERNAL_LINK, 12));
        menuItemEditCurrentFrame.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, KeyEvent.CTRL_DOWN_MASK));

        JMenuItem menuItemEditAllFrames = new JMenuItem("All Frames", IconFontSwing.buildIcon(FontAwesome.EXTERNAL_LINK, 12));
        menuItemEditAllFrames.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, KeyEvent.SHIFT_DOWN_MASK | KeyEvent.CTRL_DOWN_MASK));

        menuEdit.add(menuItemEditCurrentFrame);
        menuEdit.add(menuItemEditAllFrames);

        frame.addWindowFocusListener(new WindowAdapter() {
            @Override
            public void windowGainedFocus(WindowEvent e) {

                if (cache == null) return;

                Animation animation = viewControlPanel.getCurrentAnimation();

                if (animation == null) return;

                BufferedImage[] sprites = animation.getSprites();

                for (int i = 0; i < cache.length; i++) {
                    File file = cache[i];

                    if (file != null && file.exists())
                        try {
                            sprites[i] = ImageIO.read(file);
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                }

                try (AnimationWriter writer = new AnimationWriter(animation.getInfo().getSource())) {

                    writer.writeAnimation(animation);

                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });

        menuItemOpen.addActionListener(e -> {

            Settings settings = frame.getSettings();

            File currentAnimationsFile = settings.getFile("animationsXMLPath");

            String currentDirectory = currentAnimationsFile == null ? null : currentAnimationsFile.getParent();

            File file = promptChooseAnimationsXML("Select Animations XML File", currentDirectory);

            if (file == null)
                return;

            settings.setString("animationsXMLPath", file.getAbsolutePath());
            settings.setString("spritesRootPath", new File(file.getParent(), "Sprites").getAbsolutePath());

            DefaultTreeModel model = null;

            try {
                model = SpritesLoader.load();
            } catch (Exception e1) {
                e1.printStackTrace();

                file = promptChooseAnimationsXML("Invalid file selected. Select a valid Animations XML File", currentDirectory);

                if (file == null)
                    return;

                settings.setString("animationsXMLPath", file.getAbsolutePath());
                settings.setString("spritesRootPath", new File(file.getParent(), "Sprites").getAbsolutePath());
            }

            while (model == null) {

                try {
                    model = SpritesLoader.load();
                } catch (Exception e1) {
                    e1.printStackTrace();

                    file = promptChooseAnimationsXML("Invalid file selected. Select a valid Animations XML File", currentDirectory);

                    if (file == null)
                        return;

                    settings.setString("animationsXMLPath", file.getAbsolutePath());
                    settings.setString("spritesRootPath", new File(file.getParent(), "Sprites").getAbsolutePath());
                }
            }

            frame.setModel(model);
        });

        menuItemReloadAll.addActionListener(e -> {
            menuItemReloadAll.setEnabled(false);

            new Thread(() -> {

                setAnimation(null);
                try {
                    frame.setModel(SpritesLoader.load());
                } finally {
                    menuItemReloadAll.setEnabled(true);
                }
            }, "Thread-AnimationLoad").start();
        });

        menuItemSettings.addActionListener(e -> new SettingsDialog(frame).showDialog());

        menuItemEditCurrentFrame.addActionListener(e -> {

            Animation animation = viewControlPanel.getCurrentAnimation();

            String editorPath = frame.getSettings().getString("externalEditorPath", "");

            if (editorPath.isEmpty())
                new InfoDialog(frame, "Error", "External editor not configured yet !").showDialog();
            else
                try {
                    openInEditor(editorPath, animation, animation.getCurrentFrame());
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
        });

        menuItemEditAllFrames.addActionListener(e -> {

            Animation animation = viewControlPanel.getCurrentAnimation();

            Settings settings = frame.getSettings();

            String editorPath = settings.getString("externalEditorPath", "");

            boolean sequential = settings.getBoolean("openSequential", false);

            if (editorPath.isEmpty())
                new InfoDialog(frame, "Error", "External editor not configured yet !").showDialog();
            else
                try {
                    openAllInEditor(editorPath, animation, sequential);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
        });

        menu.add(menuFile);
        menu.add(menuEdit);

        menuEdit.setEnabled(false);

        JPanel noAnimation = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                drawString(g, "Please select an animation in the tree", TextAlign.CENTER, TextAlign.CENTER);
            }
        };

        noAnimation.setFont(noAnimation.getFont().deriveFont(Font.BOLD, 30f));

        cards.add(noAnimation, choices[0]);
        cards.add(viewControlPanel, choices[1]);

        add(menu, BorderLayout.NORTH);
        add(cards, BorderLayout.CENTER);
    }

    public void setAnimation(Animation animation) {

        if (animation == null) {
            show(0);
            menuEdit.setEnabled(false);
        } else {

            if (selected == 0) {
                show(1);
                menuEdit.setEnabled(true);
            }

            cache = new File[animation.getLength()];
        }

        viewControlPanel.setAnimation(animation);
    }

    private void show(int index) {
        selected = index;
        cl.show(cards, choices[index]);
    }

    private void openAllInEditor(String editorPath, Animation animation, boolean sequential) throws IOException {

        if (animation == null) throw new IllegalArgumentException("Animation can't be null");

        BufferedImage[] sprites = animation.getSprites();

        if (sequential)
            for (int i = 0; i < sprites.length; i++) {

                File tempFile = writeToTemp(sprites[i]);

                cache[i] = tempFile;

                ProcessBuilder builder = new ProcessBuilder(editorPath, tempFile.getAbsolutePath());

                builder.start();
            }
        else {

            String[] args = new String[sprites.length + 1];
            args[0] = editorPath;

            for (int i = 0; i < sprites.length; i++) {

                File tempFile = writeToTemp(sprites[i]);

                cache[i] = tempFile;

                args[i + 1] = tempFile.getAbsolutePath();
            }

            ProcessBuilder builder = new ProcessBuilder(args);
            builder.start();
        }
    }

    private void openInEditor(String editorPath, Animation animation, int index) throws IOException {

        if (animation == null) throw new IllegalArgumentException("Animation can't be null");

        BufferedImage[] sprites = animation.getSprites();

        File tempFile = writeToTemp(sprites[index]);

        cache[index] = tempFile;

        ProcessBuilder builder = new ProcessBuilder(editorPath, tempFile.getAbsolutePath());

        builder.start();
    }

    private File writeToTemp(BufferedImage image) throws IOException {

        String path = System.getProperty("java.io.tmpdir") + "AAETmp" + Long.toHexString(System.nanoTime()) + ".png";

        File tempFile = new File(path);

        ImageIO.write(image, "png", tempFile);

        tempFile.deleteOnExit();

        return tempFile;
    }
}
