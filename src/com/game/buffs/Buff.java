package com.game.buffs;

import com.game.GameObject;
import com.game.blocks.Block;
import com.game.entities.Entity;
import com.util.Vec2;
import com.util.collisions.AxisAlignedBoundingBox;
import com.util.collisions.StaticBoundingBox;
import com.util.visual.AnimationsHandler;
import com.util.visual.GameGraphics;

import java.awt.*;

public abstract class Buff extends GameObject implements BuffConstants {

    private final Dimension dim;

    private final AnimationsHandler handler;

    /**
     * Initialize the <code>Buff</code>.
     *
     * @param pos this <code>Buff</code>'s position
     * @param dim this <code>Buff</code>'s dimension
     */
    Buff(Vec2 pos, Dimension dim) {
        super(pos);

        this.dim = dim;

        handler = new AnimationsHandler(getClass());
    }

    static Buff createFromBlockPos(int type, int x, int y) {

        Buff buff;
        Vec2 pos = new Vec2();

        switch (type) {

            case HEAL:
                buff = new BuffHeal(pos, 10);
                break;

            case SHIELD:
                buff = new BuffShield(pos, 10);
                break;

            default:
                throw new IllegalArgumentException("Unknown type : " + type);
        }

        pos.x = x * Block.WIDTH + Block.WIDTH / 2 - buff.dim.width / 2;
        pos.y = y * Block.WIDTH + Block.WIDTH - buff.dim.height;

        buff.actualizeBoundingBox();

        return buff;
    }

    public abstract void onEntityEntered(Entity entity);

    @Override
    protected AxisAlignedBoundingBox boundingBox() {
        return new StaticBoundingBox(pos, dim.width, dim.height);
    }

    @Override
    public boolean canBeCollidedWith() {
        return false;
    }

    @Override
    protected void draw(GameGraphics g, int x, int y) {
        handler.draw(g, x, y);
    }

    @Override
    protected void drawDebug(GameGraphics g) {

        g.graphics.setColor(Color.red);

        boundingBox.draw(g);
    }

    @Override
    protected boolean drawCondition() {
        return handler.canDraw();
    }

    @Override
    protected void drawSpecial(GameGraphics g) {
        boundingBox.fill(g);
    }
}
