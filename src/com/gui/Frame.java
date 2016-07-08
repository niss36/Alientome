package com.gui;

import com.util.Config;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class Frame extends JFrame implements FocusListener {

    private static final Frame ourInstance = new Frame();
    public static final int MENU = 0, GAME = 1;
    public final PanelMainMenu panelMenu = new PanelMainMenu();
    public final PanelGame panelGame = new PanelGame();
    private final CardLayout glassCL = new CardLayout();
    private final CardLayout contentCL = new CardLayout();
    private final String[] choices = {"Menu", "Game"};
    private final JPanel glass;
    private int currentCard = MENU;

    private Frame() {

        setTitle("Alientome");
        setSize(800, 600);
        setExtendedState(getExtendedState() | MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        addFocusListener(this);

        getContentPane().setLayout(contentCL);

        getContentPane().add(panelMenu, choices[MENU]);
        getContentPane().add(panelGame, choices[GAME]);

        glass = (JPanel) getGlassPane();
        glass.setLayout(glassCL);
        glass.setVisible(true);

        glass.add(panelMenu.getGlass(), choices[MENU]);
        glass.add(panelGame.getGlass(), choices[GAME]);
    }

    public static Frame getInstance() {
        return ourInstance;
    }

    public void showCard(int index) {

        contentCL.show(getContentPane(), choices[index]);
        glassCL.show(glass, choices[index]);

        panelGame.showCard(0);
        panelMenu.showCard(0);

        currentCard = index;
    }

    @Override
    public void focusGained(FocusEvent e) {
    }

    @Override
    public void focusLost(FocusEvent e) {
        if (currentCard == GAME && Config.getInstance().getBoolean("UI.PauseOnLostFocus")) panelGame.game.pause();
    }
}
