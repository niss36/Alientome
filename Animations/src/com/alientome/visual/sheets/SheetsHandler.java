package com.alientome.visual.sheets;

import com.alientome.core.graphics.GameGraphics;
import com.alientome.core.util.Direction;

public class SheetsHandler {

    /**
     * The array of sprite sheets.
     */
    private final SpriteSheet[] sheets;

    /**
     * The index of the currently used sheet.
     */
    private int sheetUsed;

    /**
     * The index of the currently used image, in the currently used sheet.
     */
    private int spriteUsed;

    public SheetsHandler(SpriteSheet[] sheets) {
        this.sheets = sheets;
    }

    public void setVariantUsed(int sheetUsed, int spriteUsed) {
        this.sheetUsed = sheetUsed;
        this.spriteUsed = spriteUsed;
    }

    public void draw(GameGraphics g, int x, int y, Direction direction) {
        sheets[sheetUsed].draw(g.graphics, x, y, direction, spriteUsed);
    }

    public void draw(GameGraphics g, int x, int y) {
        draw(g, x, y, Direction.LEFT);
    }

    public boolean canDraw() {
        return sheets != null && sheets[sheetUsed] != null;
    }
}
