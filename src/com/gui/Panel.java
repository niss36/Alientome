package com.gui;

import com.game.Block;
import com.game.Game;
import com.game.Game.State;
import com.game.Level;
import com.game.entities.Entity;
import com.game.entities.EntityPlayer;
import com.util.Config;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Panel extends JPanel {

    //<code>CardLayout</code> constants
    public static final int BLANK = 0, MENU = 1, MENU_OPTIONS = 2, DEATH = 3;
    private static final String[] choices = {"Blank", "Menu", "MenuOptions", "Death"};
    private final CardLayout cl = new CardLayout();
    public Game game;
    private EntityPlayer player;
    private ArrayList<Entity> entities = new ArrayList<>();
    private Point min = new Point(0, 0);
    private boolean debug = false;
    private long prevTime;
    private int fps;
    private int tempFPS;
    private JPanel glass;

    public Panel() {
        super();
    }

    @Override
    public void paintComponent(Graphics g) {

        super.paintComponent(g);

        BufferedImage background = Level.getInstance().getBackground();

        g.drawImage(background, getWidth() / 2 - background.getWidth() / 2, getHeight() / 2 - background.getHeight() / 2, null);

        for (int x = min.x / Block.width - 1; x < (min.x + getWidth()) / Block.width + 1; x++)
            for (int y = min.y / Block.width - 1; y < (min.y + getHeight()) / Block.width + 1; y++)
                new Block(x, y).draw(g, min, debug);

        for (Entity entity : entities) entity.draw(g, min, debug);

        if (player != null) player.draw(g, min, debug);

        if (debug) {
            Font f = new Font(null, Font.PLAIN, 20);
            g.setFont(f);
            g.setColor(Color.red);
            g.drawString(tempFPS + "FPS", 0, 16);
        }

        if (game != null && game.state != State.RUNNING) {
            g.setColor(new Color(0, 0, 0, 128));
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    public void switchDebug() {
        debug = !debug;
    }

    public void init() {
        game = new Game(this);

        new Thread(game, "Thread-Game").start();

        glass = (JPanel) Frame.getInstance().getGlassPane();

        glass.setLayout(cl);

        glass.setVisible(true);

        JPanel blank = new JPanel(null);
        blank.setOpaque(false);
        JPanel menu = new JPanel(null);
        menu.setOpaque(false);
        JPanel menuOptions = new JPanel(null);
        menuOptions.setOpaque(false);
        JPanel death = new JPanel(null);
        death.setOpaque(false);

        Dimension d = new Dimension(200, 40);

        MenuButton options = new MenuButton(menu, "Options", d, 0, 100);
        options.addActionListener(e -> showCard(MENU_OPTIONS));

        MenuButton resume = new MenuButton(menu, "Resume", d, 0, 160);
        resume.addActionListener(e -> game.resume());

        MenuButton reset = new MenuButton(menu, "Reset", d, 0, 220);
        reset.addActionListener(e -> game.reset());

        MenuButton quit = new MenuButton(menu, "Quit", d, 0, 280);
        quit.addActionListener(e -> game.quit());

        menu.add(options);
        menu.add(resume);
        menu.add(reset);
        menu.add(quit);

        MenuLabel labelOptions = new MenuLabel(menuOptions, "Controls", d, 0, 40, MenuLabel.CENTER);

        menuOptions.add(labelOptions);

        String[] labels = {"Jump : ", "Move Left : ", "Move Right : ", "Fire : ", "Debug : "};

        for (int i = 0; i < labels.length; i++) {
            MenuLabel label = new MenuLabel(menuOptions, labels[i], d, -100, 100 + i * 60, MenuLabel.RIGHT);
            MenuKeyButton button = new MenuKeyButton(menuOptions, d, 100, 100 + i * 60, i);
            Config.getInstance().addConfigListener(button);
            menuOptions.add(label);
            menuOptions.add(button);
        }

        MenuButton buttonReset = new MenuButton(menuOptions, "Reset controls", d, 0, 100 + labels.length * 60);
        buttonReset.addActionListener(e1 -> Config.getInstance().reset());

        MenuButton buttonBack = new MenuButton(menuOptions, "Done", d, 0, 100 + (labels.length + 1) * 60);
        buttonBack.addActionListener(e -> showCard(MENU));

        menuOptions.add(buttonReset);
        menuOptions.add(buttonBack);

        menuOptions.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == Config.getInstance().getKey("Key.Pause") && !MenuKeyButton.inUse)
                    buttonBack.doClick();
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });

        MenuLabel labelDeath = new MenuLabel(death, "You died", d, 0, 40, MenuLabel.CENTER);

        MenuButton respawn = new MenuButton(death, "Respawn", d, -120, 100);
        respawn.addActionListener(e -> game.reset());

        MenuButton quit0 = new MenuButton(death, "Quit", d, 120, 100);
        quit0.addActionListener(e -> game.quit());

        death.add(labelDeath);
        death.add(respawn);
        death.add(quit0);

        death.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE) respawn.doClick();

                else if (e.getKeyCode() == Config.getInstance().getKey("Key.Pause")) quit0.doClick();
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });

        glass.add(blank, choices[BLANK]);
        glass.add(menu, choices[MENU]);
        glass.add(menuOptions, choices[MENU_OPTIONS]);
        glass.add(death, choices[DEATH]);
    }

    public void showCard(int index) {

        for (State state : State.values()) if (state.ordinal() == index) game.state = state;

        cl.show(glass, choices[index]);

        if(index == BLANK) Frame.getInstance().requestFocus();

        else glass.getComponents()[index].requestFocus();
    }

    public void update(EntityPlayer player, ArrayList<Entity> entities) {

        if (System.currentTimeMillis() - prevTime >= 1000) {
            prevTime = System.currentTimeMillis();
            tempFPS = fps;
            fps = 0;
        } else {
            fps++;
        }

        min = new Point((int) player.getX() - getWidth() / 2,
                (int) player.getY() - getHeight() / 2);

        if (min.x < 0) min.x = 0;

        if (min.y < 0) min.y = 0;

        player.onUpdate();

        //noinspection ForLoopReplaceableByForEach
        for (int i = 0; i < entities.size(); i++) entities.get(i).onUpdate();

        this.player = player;
        this.entities = entities;

        repaint();
    }
}
