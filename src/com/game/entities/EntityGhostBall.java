package com.game.entities;

import com.util.Direction;
import com.util.Side;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class EntityGhostBall extends EntityProjectile {

    private static final BufferedImage[] sprites = new BufferedImage[2];
    private static boolean init = false;

    EntityGhostBall(Entity thrower) {

        super(thrower, new Dimension(8, 8));

        maxVelocity = 10;

        gravity = false;

        if (!init) try {
            for (int i = 0; i < 2; i++)
                sprites[i] = ImageIO.read(ClassLoader.getSystemResourceAsStream("GhostBall/" + i + ".png"));
            init = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpdate() {

        move(facing, 5);

        super.onUpdate();
    }

    @Override
    public void notifyCollision(Entity other, Side side) {

    }

    @Override
    public void draw(Graphics g, Point min, boolean debug) {

        int x = (int) this.x - min.x;
        int y = (int) this.y - min.y;

        super.drawAnimated(g, sprites, x + (facing == Direction.LEFT ? 0 : -8), y);

        if (debug) drawHitBox(g, x, y);
    }
}
