package com.game.buffs;

import com.game.GameObject;
import com.game.blocks.Block;
import com.game.entities.Entity;
import com.util.AxisAlignedBB;
import com.util.visual.AnimationsHandler;

import java.awt.*;

public abstract class Buff extends GameObject implements BuffConstants {

    private final Dimension dim;

    private final AnimationsHandler handler;

    /**
     * Initialize the <code>Buff</code>.
     *
     * @param x     the x coordinate
     * @param y     the y coordinate
     * @param dim   this <code>Buff</code>'s dimension
     */
    Buff(int x, int y, Dimension dim) {
        super(x * Block.width, y * Block.width);

        this.dim = dim;

        handler = new AnimationsHandler(getClass());
    }

    static Buff createFromBlockPos(int type, int x, int y) {

        Buff buff;

        switch (type) {

            case HEAL:
                buff = new BuffHeal(0, 0, 10);
                break;

            case SHIELD:
                buff = new BuffShield(0, 0, 10);
                break;

            default:
                return null;
        }

        double x0 = x * Block.width + Block.width / 2 - buff.dim.width / 2;
        double y0 = y * Block.width + Block.width - buff.dim.height;

        buff.pos.set(x0, y0);

        buff.actualizeBoundingBox();

        return buff;
    }

    public abstract void entityEntered(Entity entity);

    @Override
    protected AxisAlignedBB boundingBox() {
        return new AxisAlignedBB(pos, dim.width, dim.height);
    }

    @Override
    public boolean canBeCollidedWith() {
        return false;
    }

    @Override
    protected void draw(Graphics g, int x, int y) {

        handler.draw(g, x, y);
    }

    @Override
    protected void drawDebug(Graphics g, Point origin) {

        g.setColor(Color.red);

        boundingBox.draw(g, origin);
    }

    @Override
    protected boolean drawCondition() {

        return handler.canDraw();
    }

    @Override
    protected void drawSpecial(Graphics g, Point origin) {

        g.setColor(Color.black);

        boundingBox.fill(g, origin);
    }
}
