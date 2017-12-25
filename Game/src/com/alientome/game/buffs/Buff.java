package com.alientome.game.buffs;

import com.alientome.core.SharedInstances;
import com.alientome.core.collisions.AxisAlignedBoundingBox;
import com.alientome.core.graphics.GameGraphics;
import com.alientome.core.util.Vec2;
import com.alientome.game.GameObject;
import com.alientome.game.SpritesLoader;
import com.alientome.game.blocks.Block;
import com.alientome.game.buffs.parse.BuffState;
import com.alientome.game.collisions.StaticBoundingBox;
import com.alientome.game.entities.Entity;
import com.alientome.game.registry.GameRegistry;
import com.alientome.game.util.Util;
import com.alientome.visual.animations.AnimationsHandler;

import java.awt.*;

import static com.alientome.core.SharedNames.REGISTRY;
import static com.alientome.core.util.Colors.DEBUG;

public abstract class Buff extends GameObject {

    private final Dimension dim;

    private final AnimationsHandler handler;

    /**
     * Initialize the <code>Buff</code>.
     *
     * @param pos this <code>Buff</code>'s position
     * @param dim this <code>Buff</code>'s dimension
     */
    protected Buff(Vec2 pos, Dimension dim) {
        super(pos);

        this.dim = dim;

        handler = SpritesLoader.newAnimationsHandlerFor(getClass());
    }

    public static Buff create(BuffState state) {

        if (state == null)
            throw new IllegalArgumentException("Null buff state");

        GameRegistry registry = SharedInstances.get(REGISTRY);

        Class<?>[] constructorTypes = new Class[state.args.length + 1];
        constructorTypes[0] = Vec2.class;
        for (int i = 0; i < state.args.length; i++)
            constructorTypes[i + 1] = state.args[i].getClass();

        Vec2 pos = new Vec2();

        Object[] args = new Object[state.args.length + 1];
        args[0] = pos;
        System.arraycopy(state.args, 0, args, 1, state.args.length);

        Buff buff = Util.create(registry.getBuffsRegistry(), state.identifier, constructorTypes, args);

        pos.x = state.spawnX * Block.WIDTH + Block.WIDTH / 2 - buff.dim.width / 2;
        pos.y = state.spawnY * Block.WIDTH + Block.WIDTH - buff.dim.height;

        buff.actualizeBoundingBox();

        return buff;

        /*Class<Buff> buffClass = null;

        try {

            GameRegistry registry = SharedInstances.get(REGISTRY);

            buffClass = registry.getBuffsRegistry().get(state.identifier);

            if (buffClass == null)
                throw new IllegalArgumentException("Unregistered Buff id : " + state.identifier);

            Constructor<Buff> constructor = (Constructor<Buff>) buffClass.getConstructors()[0];

            Vec2 pos = new Vec2();

            Object[] args = new Object[state.args.length + 1];

            args[0] = pos;
            System.arraycopy(state.args, 0, args, 1, state.args.length);

            Buff buff = constructor.newInstance(args);

            pos.x = x * Block.WIDTH + Block.WIDTH / 2 - buff.dim.width / 2;
            pos.y = y * Block.WIDTH + Block.WIDTH - buff.dim.height;

            buff.actualizeBoundingBox();

            return buff;

        } catch (IllegalAccessException | InstantiationException e) {
            throw new IllegalArgumentException("Class " + buffClass.getName() + " does not correctly implement expected constructor", e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }*/
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

        g.graphics.setColor(DEBUG);

        boundingBox.draw(g);
    }

    @Override
    protected boolean canDraw() {
        return handler.canDraw();
    }

    @Override
    protected void drawSpecial(GameGraphics g, int x, int y) {
        boundingBox.fill(g);
    }
}
