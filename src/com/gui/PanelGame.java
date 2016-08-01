package com.gui;

import com.game.Game;
import com.game.Game.State;
import com.game.level.Level;
import com.util.Config;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class PanelGame extends JPanel {

    //CardLayout constants
    private static final String[] choices = {"Blank", "Menu", "MenuControls", "MenuOptions", "Death"};
    public static final int BLANK = 0, MENU = 1, MENU_CONTROLS = 2, MENU_OPTIONS = 3, DEATH = 4;
    public final Game game = Game.getInstance();
    private final CardLayout cl = new CardLayout();
    private final JPanel glass;
    private boolean debug = false;
    private long prevTime;
    private int fps;
    private int tempFPS;
    private int updatedCells;

    PanelGame() {
        super();

        game.init(this);

        glass = new JPanel();

        glass.setLayout(cl);

        glass.setOpaque(false);

        JPanel blank = new JPanel(null);
        blank.setOpaque(false);


        Dimension d = new Dimension(200, 40);

        String[] menuNames = {
                "Controls",
                "Options",
                "Resume",
                "Reset Level",
                "Exit to Menu"};

        ActionListener[] menuListeners = {
                e -> showCard(MENU_CONTROLS),
                e -> showCard(MENU_OPTIONS),
                e -> game.resume(),
                e -> game.reset(),
                e -> game.exit()};

        JPanel menu = MenuUtility.createMenu(d, "Pause menu", menuNames, menuListeners);


        String[] controlsLabels = {"Jump : ", "Move Left : ", "Move Right : ", "Fire : ", "Debug : "};

        MenuButton[] controlsButtons = new MenuButton[controlsLabels.length];

        for (int i = 0; i < controlsButtons.length; i++) {
            MenuKeyButton button = new MenuKeyButton(d, i);
            controlsButtons[i] = button;
        }

        MenuButton buttonReset = new MenuButton("Reset controls", d);
        buttonReset.addActionListener(e -> Config.getInstance().resetKeys());

        MenuButton buttonBack = new MenuButton("Done", d);
        buttonBack.addActionListener(e -> showCard(MENU));

        JPanel menuControls = MenuUtility.createLabelledMenu(d, "Controls", controlsLabels, controlsButtons, buttonReset, buttonBack);

        menuControls.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == Config.getInstance().getInt("Key.Pause") && !MenuKeyButton.inUse)
                    buttonBack.doClick();
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });


        String[] optionsLabels = {"Pause on lost focus : ", "Show block in : ", "Show lines of sight : "};

        MenuButton[] optionsButtons = new MenuButton[optionsLabels.length];

        for (int i = 0; i < optionsButtons.length; i++) {

            MenuSwitchButton button = new MenuSwitchButton(d, i);
            Config.getInstance().addConfigListener(button);
            optionsButtons[i] = button;
        }

        MenuButton buttonReset0 = new MenuButton("Reset options", d);
        buttonReset0.addActionListener(e -> Config.getInstance().reset());

        MenuButton buttonBack0 = new MenuButton("Done", d);
        buttonBack0.addActionListener(e -> showCard(MENU));

        JPanel menuOptions = MenuUtility.createLabelledMenu(new Dimension(300, 40), "Options", optionsLabels, optionsButtons, buttonReset0, buttonBack0);

        menuOptions.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == Config.getInstance().getInt("Key.Pause")) buttonBack0.doClick();
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });


        MenuButton respawn = new MenuButton("Respawn", d);
        respawn.addActionListener(e -> game.reset());

        MenuButton quit = new MenuButton("Exit to Menu", d);
        quit.addActionListener(e -> game.exit());

        JPanel death = MenuUtility.createChoiceMenu(d, "You died", 40, respawn, quit);

        death.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE) respawn.doClick();

                else if (e.getKeyCode() == Config.getInstance().getInt("Key.Pause")) quit.doClick();
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });

        glass.add(blank, choices[BLANK]);
        glass.add(menu, choices[MENU]);
        glass.add(menuControls, choices[MENU_CONTROLS]);
        glass.add(menuOptions, choices[MENU_OPTIONS]);
        glass.add(death, choices[DEATH]);
    }

    @Override
    public void paintComponent(Graphics g) {

        super.paintComponent(g);

        Level.getInstance().draw(g, debug);

        if (debug) {
            Font f = new Font(null, Font.PLAIN, 20);
            g.setFont(f);
            g.setColor(Color.red);
            g.drawString(tempFPS + "FPS", 0, 16);
            g.drawString(updatedCells + " cell updates", 0, 40);
        }

        if (game != null && game.state != State.RUNNING) {
            g.setColor(new Color(0, 0, 0, 128));
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    public void switchDebug() {
        debug = !debug;
    }

    public void showCard(int index) {

        for (State state : State.values()) if (state.ordinal() == index) game.state = state;

        cl.show(glass, choices[index]);

        if (index == BLANK) Frame.getInstance().requestFocus();

        else glass.getComponents()[index].requestFocus();
    }

    JPanel getGlass() {
        return glass;
    }

    public void update(int updatedCells) {

        if (System.currentTimeMillis() - prevTime >= 1000) {
            prevTime = System.currentTimeMillis();
            tempFPS = fps;
            fps = 0;
        } else {
            fps++;
        }

        this.updatedCells = updatedCells;

        repaint();
    }
}
