package com.gui;

import com.game.Block;
import com.game.Game;
import com.game.entities.Entity;
import com.game.entities.EntityPlayer;

import javax.swing.*;
import java.awt.*;
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

    public void paintComponent(Graphics g) {

        super.paintComponent(g);

        for (int x = min.x / Block.width - 1; x < (min.x + getWidth()) / Block.width + 1; x++)
            for (int y = min.y / Block.width - 1; y < (min.y + getHeight()) / Block.width + 1; y++)
                new Block(x, y).draw(g, min, debug);

        for (Entity entity : entities) entity.draw(g, min, debug);

        if (player != null) player.draw(g, min, debug);

        if (debug) {
            Font f = new Font(null, Font.PLAIN, 20);
            g.setFont(f);
            g.setColor(Color.red);
            g.drawString(tempFPS + "FPS", 0, 15);
        }
    }

    public void switchDebug() {
        debug = !debug;
    }

    public void init() {
        game = new Game(this);

        new Thread(game, "Thread-Game").start();
    }

    public void update(EntityPlayer player, ArrayList<Entity> entities) {

        if (prevTime == 0 || System.currentTimeMillis() - prevTime >= 1000) {
            prevTime = System.currentTimeMillis();
            tempFPS = fps;
            fps = 0;
        } else {
            fps++;
        }

        min = new Point((int) player.getX() - getWidth() / 2,
                (int) player.getY() - getHeight() / 2);

        player.onUpdate();

        //noinspection ForLoopReplaceableByForEach
        for (int i = 0; i < entities.size(); i++) entities.get(i).onUpdate();

        this.player = player;
        this.entities = entities;

        repaint();
    }
}
