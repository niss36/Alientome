package com.alientome.game.blocks;

import com.alientome.core.graphics.GameGraphics;
import com.alientome.game.SpritesLoader;
import com.alientome.game.blocks.parse.BlockState;
import com.alientome.visual.animations.AnimationsHandler;

public abstract class BlockAnimation extends Block {

    protected final AnimationsHandler handler;

    public BlockAnimation(int x, int y, BlockState state) {

        super(x, y, state);

        handler = SpritesLoader.newAnimationsHandlerFor(getClass());
    }

    @Override
    protected void draw(GameGraphics g, int x, int y) {
        handler.draw(g, x, y);
    }

    @Override
    protected boolean canDraw() {
        return handler.canDraw();
    }
}
