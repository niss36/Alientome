package com.alientome.game.blocks;

import com.alientome.core.graphics.GameGraphics;
import com.alientome.game.SpritesLoader;
import com.alientome.game.blocks.parse.BlockState;
import com.alientome.visual.sheets.SheetsHandler;

public abstract class BlockSheet extends Block {

    protected final SheetsHandler handler;

    public BlockSheet(int x, int y, BlockState state) {

        super(x, y, state);

        handler = SpritesLoader.newSheetsHandlerFor(getClass());
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
