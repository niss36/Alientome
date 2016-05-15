package com.gui;

import com.game.Block;
import com.game.Game;
import com.game.Level;
import com.game.entities.Entity;
import com.game.entities.EntityPlayer;
import com.game.Game.State;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Panel extends JPanel {

    public Game game;

    private EntityPlayer player;
    private ArrayList<Entity> entities = new ArrayList<>();
    private Point min = new Point(0, 0);

    private boolean debug = false;

    private long prevTime;
    private int fps;
    private int tempFPS;


    CardLayout cl = new CardLayout();


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

        if(game != null && game.state != State.RUNNING) {
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

        setLayout(null);

        int x = getWidth() / 2 - 100;

        int y = 380;

        MenuButton options = new MenuButton("Options", game, new Rectangle(x, 200, 200, 40), State.PAUSED);
        options.addActionListener(e -> System.out.println("Options"));

        MenuButton resume = new MenuButton("Resume", game, new Rectangle(x, 260, 200, 40), State.PAUSED);
        resume.addActionListener(e -> game.resume());

        MenuButton reset = new MenuButton("Reset", game, new Rectangle(x, 320, 200, 40), State.PAUSED);
        reset.addActionListener(e -> game.reset());

        MenuButton quit = new MenuButton("Quit", game, new Rectangle(x, 380, 200, 40), State.PAUSED);
        quit.addActionListener(e -> game.quit());

        MenuLabel labelDeath = new MenuLabel("You died", game, new Rectangle(x, y, 200, 40), State.DEATH);

        MenuButton respawn = new MenuButton("Respawn", game, new Rectangle(x - 120, y + 60, 200, 40), State.DEATH);
        respawn.addActionListener(e -> game.reset());

        MenuButton quit0 = new MenuButton("Quit", game, new Rectangle(x + 120, y + 60, 200, 40), State.DEATH);
        quit0.addActionListener(e -> game.quit());

        add(options);
        add(resume);
        add(reset);
        add(quit);

        add(labelDeath);
        add(respawn);
        add(quit0);
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
