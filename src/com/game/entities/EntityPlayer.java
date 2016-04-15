package com.game.entities;

import com.game.level.Level;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class EntityPlayer extends EntityLiving {

    private static BufferedImage[] sprites;
    private static BufferedImage[] spritesCharging;
    private static BufferedImage[] spritesCharged;
    private int ghostBallCoolDown = 0;

    private boolean charging = false;
    private boolean charged = false;
    private float chargeState;

    public EntityPlayer(int x, int y, Level level) {

        super(x, y, new Dimension(20, 31), level, 20);

        if (sprites == null) sprites = getSpritesAnimated("Alientome", 2);
        if (spritesCharging == null) spritesCharging = getSpritesAnimated("Alientome/Charge", 5);
        if (spritesCharged == null) spritesCharged = getSpritesAnimated("Alientome/Charged", 2);
    }

    @Override
    public void onUpdate() {

        super.onUpdate();

        if (ghostBallCoolDown > 0) ghostBallCoolDown--;

        if (charging) {
            chargeState = chargeState >= 4 ? 4f : chargeState + 0.1f;
            charged = chargeState >= 4 || charged;
            maxVelocity = Math.max(5 - (int) chargeState, 2);
        } else {
            maxVelocity = 5;
        }
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

        if (charged) drawAnimated(g, spritesCharged, x, y, 10);

        else if (charging) drawImage(g, spritesCharging[(int) chargeState], x, y);

        else drawAnimated(g, sprites, x, y, 10);

        if (debug) drawBoundingBox(g, x, y);

        g.setColor(Color.black);

        drawHealthBar(g, min);
    }

    private void throwGhostBall(boolean big) {

        if (ghostBallCoolDown == 0) {

            level.spawnEntity(new EntityGhostBall(this, big));

            ghostBallCoolDown = 33;
        }
    }

    public void startCharging() {

        if (ghostBallCoolDown == 0 && !charging) {
            charging = true;
            charged = false;
            chargeState = 0;
        }
    }

    public void stopCharging() {
        if (charging) {
            throwGhostBall(charged);
            charging = false;
            charged = false;
        }
    }
}
