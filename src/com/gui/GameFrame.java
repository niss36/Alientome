package com.gui;

import com.events.GameEventDispatcher;
import com.settings.Config;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import static com.events.GameEventType.*;

public class GameFrame extends JFrame implements FocusListener {

    private static final int MENU = 0, GAME = 1;
    private final PanelMainMenu panelMenu;
    private final PanelGame panelGame;
    private final CardLayout glassCL = new CardLayout();
    private final CardLayout contentCL = new CardLayout();
    private final String[] choices = {"Menu", "Game"};
    private final JComponent glass;
    private int currentCard = MENU;

    public GameFrame(boolean debugUI) {

        super("Alientome");

        GameComponent.debugUI = debugUI;

        panelMenu = new PanelMainMenu();
        panelGame = new PanelGame();

        GameDialog.gameFrame = this;

        setLocationRelativeTo(null);
        setResizable(false);
        setUndecorated(true);
        setExtendedState(getExtendedState() | MAXIMIZED_BOTH);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        addFocusListener(this);

        getContentPane().setLayout(contentCL);

        getContentPane().add(panelMenu, choices[MENU]);
        getContentPane().add(panelGame, choices[GAME]);

        glass = (JComponent) getGlassPane();
        glass.setLayout(glassCL);
        glass.setVisible(true);

        glass.add(panelMenu.getGlass(), choices[MENU]);
        glass.add(panelGame.getGlass(), choices[GAME]);

        GameEventDispatcher dispatcher = GameEventDispatcher.getInstance();
        dispatcher.register(GAME_START, e -> showCard(GAME));
        dispatcher.register(GAME_EXIT, e -> showCard(MENU));
    }

    private void showCard(int index) {

        panelGame.showBaseMenu();
        panelMenu.showBaseMenu();

        contentCL.show(getContentPane(), choices[index]);
        glassCL.show(glass, choices[index]);

        currentCard = index;
    }

    @Override
    public void focusGained(FocusEvent e) {
    }

    @Override
    public void focusLost(FocusEvent e) {
        if (currentCard == GAME && Config.getInstance().getBoolean("pauseOnLostFocus"))
            GameEventDispatcher.getInstance().submit(null, GAME_PAUSE);
    }
}
