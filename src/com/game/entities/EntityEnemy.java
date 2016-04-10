package com.game.entities;

import com.game.Block;
import com.game.entities.ai.*;
import com.game.level.Level;
import com.util.Direction;
import com.util.Side;

import java.awt.*;

public class EntityEnemy extends EntityLiving {

    private final int followRange;
    private boolean stuck;

    private AI ai;

    public EntityEnemy(int x, int y, Level level, int followRange) {

        super(x, y, new Dimension(20, 30), level, 10);

        maxVelocity = 3;

        this.followRange = entityRandom.nextInt(50) + followRange - 25;

        ai = new AIRepeat(new AIFollow(this, level.player, followRange, false));
    }

    @Override
    public void onUpdate() {

        if(ai.getState() == null) ai.start();
        ai.act();

        super.onUpdate();

        /*double xDif = x - level.player.x;
        double yDif = y - level.player.y;

        if (Math.sqrt(xDif * xDif + yDif * yDif) <= followRange) {

            if (level.player.x < x) move(Direction.LEFT);
            if (level.player.x > x) move(Direction.RIGHT);

            if (collidedX && level.player.x != x) {
                if (!stuck) jump();
                else
                    stuck = true;
            }

            boolean b = false;

            for (int i = 0; i < 2; i++)
                b = new Block((int) ((x + motionX + i * dim.width - i) / blockWidth), (int) ((y + 1 + dim.height) / blockWidth)).isOpaque() || b;

            if (!b && level.player.y <= y) jump();
        }*/
    }

    @Override
    public boolean onCollidedWithEntity(Entity other, Side side) {
        if (super.onCollidedWithEntity(other, side)) {

            if (other instanceof EntityPlayer) {
                if (side == Side.BOTTOM) {
                    damage(10);
                    if(other.onGround) other.motionY = -11;
                }
                else ((EntityPlayer) other).damage(1);
            }

            return true;
        }

        return false;
    }

    @Override
    public void draw(Graphics g, Point min, boolean debug) {

        g.setColor(Color.orange);

        super.draw(g, min, debug);
    }
}
