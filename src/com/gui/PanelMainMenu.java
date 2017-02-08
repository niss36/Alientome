package com.gui;

import com.keybindings.InputManager;
import com.settings.Config;
import com.util.GameFont;
import com.util.I18N;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.BufferedInputStream;
import java.io.IOException;

import static com.util.Util.makeListener;

class PanelMainMenu extends JPanel implements ComponentListener {

    private final MenuInterface menuInterface;
    private final Clip bgMusic;
    private String versionStr;

    PanelMainMenu() {

        setFont(GameFont.get(2));

        setVersion();
        I18N.addLangChangedListener(this::setVersion);

        Dimension d = new Dimension(340, 70);
        Font f = GameFont.get(5);

        menuInterface = new MenuInterface(d, f, "main");

        InputManager.getInstance().setListener("menu.main", "back", makeListener(menuInterface::popMenu));

        Clip clip = null;

        try (AudioInputStream inputStream = AudioSystem.getAudioInputStream(new BufferedInputStream(ClassLoader.getSystemResourceAsStream("Audio/main0.wav")))) {
            clip = AudioSystem.getClip();
            clip.open(inputStream);

            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);

            Config.getInstance().addSettingListener("volume", newValue -> setVolume(gainControl, (int) newValue));
            setVolume(gainControl, Config.getInstance().getInt("volume"));

            clip.loop(Clip.LOOP_CONTINUOUSLY);

        } catch (LineUnavailableException | UnsupportedAudioFileException | IOException e) {
            e.printStackTrace();
        }

        bgMusic = clip;

        addComponentListener(this);
    }

    private void setVersion() {

        versionStr = I18N.getStringFormatted("menu.main.version", Config.getInstance().getString("version"));
        repaint();
    }

    private void setVolume(FloatControl gainControl, int value) {

        float volume = value / 100f;
        gainControl.setValue((float) (Math.log(volume) / Math.log(10.0) * 20.0));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.drawString(versionStr, 5, getHeight() - 5);
    }

    void showBaseMenu() {
        menuInterface.showBase();
    }

    JComponent getGlass() {

        return menuInterface;
    }

    @Override
    public void componentResized(ComponentEvent e) {

    }

    @Override
    public void componentMoved(ComponentEvent e) {

    }

    @Override
    public void componentShown(ComponentEvent e) {
        if (bgMusic != null)
            bgMusic.loop(Clip.LOOP_CONTINUOUSLY);
    }

    @Override
    public void componentHidden(ComponentEvent e) {
        if (bgMusic != null)
            bgMusic.stop();
    }
}
