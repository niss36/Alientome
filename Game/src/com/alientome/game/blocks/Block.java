package com.alientome.game.blocks;

import com.alientome.core.SharedInstances;
import com.alientome.core.collisions.Contact;
import com.alientome.core.graphics.GameGraphics;
import com.alientome.game.GameObject;
import com.alientome.game.blocks.component.BlockTypeComponent;
import com.alientome.game.blocks.parse.BlockState;
import com.alientome.game.collisions.AbstractBoundingBox;
import com.alientome.game.collisions.StaticBoundingBox;
import com.alientome.game.entities.Entity;
import com.alientome.game.registry.GameRegistry;
import com.alientome.game.util.Util;

import static com.alientome.core.SharedNames.REGISTRY;
import static com.alientome.core.util.Colors.DEBUG;

/**
 * Used to represent tiles in the Level.
 */
public abstract class Block extends GameObject implements BlockConstants {

    public final int blockX;
    public final int blockY;
    protected final String identifier;
    protected final byte metadata;
    protected final String debugInfo;
    private final BlockTypeComponent type;

    /**
     * Initialize the Block
     *
     * @param x     the x coordinate
     * @param y     the y coordinate
     * @param state the state of this block
     */
    protected Block(int x, int y, BlockState state) {

        super(x * WIDTH, y * WIDTH);

        blockX = x;
        blockY = y;
        identifier = state.identifier;
        metadata = state.metadata;
        debugInfo = identifier + ":" + metadata;

        actualizeBoundingBox();

        type = createTypeComponent();
    }

    public static Block create(int x, int y, BlockState state) {

        if (state == null)
            return create(x, y, new BlockState("void", (byte) 0));

        GameRegistry registry = SharedInstances.get(REGISTRY);
        Class<?>[] constructorTypes = {Integer.TYPE, Integer.TYPE, BlockState.class};
        Object[] args = {x, y, state};

        return Util.create(registry.getBlocksRegistry(), state.identifier, constructorTypes, args);
    }

    protected abstract BlockTypeComponent createTypeComponent();

    /**
     * @return whether this <code>Block</code> is opaque and can't be seen through
     */
    public boolean isOpaque() {
        return type.isOpaque();
    }

    @Override
    public boolean canBeCollidedWith() {
        return type.canBeCollidedWith();
    }

    public int beforeCollide(Entity entity, Contact contact) {
        return type.beforeCollide(entity, contact);
    }

    @SuppressWarnings("SuspiciousNameCombination")
    @Override
    protected AbstractBoundingBox boundingBox() {
        return new StaticBoundingBox(pos, WIDTH, WIDTH);
    }

    @Override
    protected void drawDebug(GameGraphics g) {

        int x = getScaledX(g.origin);
        int y = getScaledY(g.origin);

        g.graphics.setColor(DEBUG);

        g.graphics.drawString(debugInfo, x + 2, y + 12);

        boundingBox.draw(g);
    }

    @Override
    protected void drawSpecial(GameGraphics g, int x, int y) {
        if (isOpaque()) boundingBox.fill(g);
    }

    @Override
    public String toString() {
        return String.format("Block [blockX=%s, blockY=%s, id=%s, metadata=%s]", blockX, blockY, identifier, metadata);
    }
}
