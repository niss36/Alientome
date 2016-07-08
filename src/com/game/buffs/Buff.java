package com.game.buffs;

import com.game.GameObject;
import com.game.blocks.Block;
import com.game.entities.Entity;
import com.game.level.Level;
import com.util.AxisAlignedBB;
import com.util.visual.AnimationsHandler;

import java.awt.*;

public abstract class Buff extends GameObject implements BuffConstants {

    private final Level level;
    private final Dimension dim;

    private final AnimationsHandler handler;

    /**
     * Initialize the <code>Buff</code>.
     *
     * @param x     the x coordinate
     * @param y     the y coordinate
     * @param dim   this <code>Buff</code>'s dimension
     * @param level the <code>Level</code> this <code>Buff</code> is in
     */
    Buff(int x, int y, Dimension dim, Level level) {
        super(x * Block.width, y * Block.width);

        this.dim = dim;
        this.level = level;

        handler = new AnimationsHandler(getClass());
    }

    public static Buff createFromBlockPos(int type, int x, int y, Level level) {

        Buff buff;

        switch (type) {

            case HEAL:
                buff = new BuffHeal(0, 0, level, 10);
                break;

            case SHIELD:
                buff = new BuffShield(0, 0, level, 10);
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

    /**
     * Sets the current animation.
     *
     * @param index the index of the <code>Animation</code> in this <code>Entity</code>'s animations.
     */
    void setAnimationInUse(int index) {
        handler.setAnimationUsed(index);
    }

    @Override
    protected AxisAlignedBB boundingBox() {
        return new AxisAlignedBB(pos.x, pos.y, pos.x + dim.width, pos.y + dim.height);
    }

    @Override
    protected void draw(Graphics g, int x, int y) {

        handler.draw(g, x, y);
    }

    @Override
    protected void drawDebug(Graphics g, Point min) {

        g.setColor(Color.red);

        boundingBox.draw(g, min);
    }

    @Override
    protected boolean drawCondition() {

        return handler.canDraw();
    }

    @Override
    protected void drawSpecial(Graphics g, Point min) {

        g.setColor(Color.black);

        boundingBox.fill(g, min);
    }
}
