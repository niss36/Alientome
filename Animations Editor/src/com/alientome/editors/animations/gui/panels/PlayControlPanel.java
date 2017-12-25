package com.alientome.editors.animations.gui.panels;

import com.alientome.editors.animations.util.PlayState;
import jiconfont.icons.FontAwesome;
import jiconfont.swing.IconFontSwing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

public class PlayControlPanel extends JPanel {

    private PlayState state = PlayState.PLAYING;

    public PlayControlPanel(AnimationViewPanel controlled) {

        BoxLayout boxLayout = new BoxLayout(this, BoxLayout.X_AXIS);
        setLayout(boxLayout);

        Icon play = IconFontSwing.buildIcon(FontAwesome.PLAY, 12);
        Icon pause = IconFontSwing.buildIcon(FontAwesome.PAUSE, 12);

        JButton playPauseButton = new JButton(pause);
        JButton stepBackwardButton = new JButton(IconFontSwing.buildIcon(jiconfont.icons.FontAwesome.STEP_BACKWARD, 12));
        JButton stepForwardButton = new JButton(IconFontSwing.buildIcon(jiconfont.icons.FontAwesome.STEP_FORWARD, 12));

        playPauseButton.addActionListener(e -> {
            switch (state) {
                case PLAYING:
                    state = PlayState.PAUSED;
                    playPauseButton.setIcon(play);
                    break;

                case PAUSED:
                    state = PlayState.PLAYING;
                    playPauseButton.setIcon(pause);
                    break;
            }

            controlled.setState(state);
        });

        stepBackwardButton.addActionListener(e -> controlled.previousFrame());
        stepForwardButton.addActionListener(e -> controlled.nextFrame());

        add(stepBackwardButton);
        add(Box.createRigidArea(new Dimension(10, 0)));
        add(playPauseButton);
        add(Box.createRigidArea(new Dimension(10, 0)));
        add(stepForwardButton);

        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(e -> {
            if (e.getID() == KeyEvent.KEY_PRESSED) {

                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    playPauseButton.doClick();
                    return true;
                } else if ((e.getModifiersEx() & KeyEvent.SHIFT_DOWN_MASK) != 0)
                    if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                        stepBackwardButton.doClick();
                        return true;
                    } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                        stepForwardButton.doClick();
                        return true;
                    }
            }
            return false;
        });
    }
}
