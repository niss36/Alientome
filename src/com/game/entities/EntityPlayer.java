package com.game.entities;

import com.game.level.Level;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class EntityPlayer extends EntityLiving {

    private static final BufferedImage[] sprites = new BufferedImage[2];
    private static boolean init = false;
    private int ghostBallCoolDown = 0;

    public EntityPlayer(int x, int y, Level level) {

        super(x, y, new Dimension(20, 31), level, 20);

        if (!init) try {
            for (int i = 0; i < 2; i++)
                sprites[i] = ImageIO.read(ClassLoader.getSystemResourceAsStream("Alientome/" + i + ".png"));
            init = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpdate() {

        super.onUpdate();

        if (ghostBallCoolDown > 0) ghostBallCoolDown--;
    }

    @Override
    public void onDeath() {

        String[] options = {"Respawn", "Quit"};

        int i = JOptionPane.showOptionDialog(null, "You died.", "Oops", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

        if (i == JOptionPane.YES_OPTION) level.reset();
        else System.exit(0);
    }

    @Override
    public void draw(Graphics g, Point min, boolean debug) {

        int x = (int) this.x - min.x;
        int y = (int) this.y - min.y;

        super.drawAnimated(g, sprites, x, y);

        if (debug) drawHitBox(g, x, y);

        g.setColor(Color.black);

        drawHealthBar(g, min);
    }

    public void throwGhostBall() {

        if (ghostBallCoolDown == 0) {

            level.spawnEntity(new EntityGhostBall(this));

            ghostBallCoolDown = 33;
        }
    }
}
