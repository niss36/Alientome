package com.alientome.core.graphics;

import java.awt.*;

/**
 * A GameGraphics object wraps around several objects needed to render game components.
 */
public class GameGraphics {

    /**
     * The actual Graphics with drawing capabilities.
     */
    public final Graphics graphics;

    /**
     * A point holding the game coordinates corresponding to the top-left pixel, that is to say screen coordinates (0; 0).
     * It serves as a way to map game coordinates to screen coordinates.
     */
    public final Point origin;

    /**
     * The number of ticks (game updates) that have elapsed since the last level reset.
     */
    public final long currentTimeTicks;

    /**
     * A number between 0.0 and 1.0 (inclusive) indicating the partial tick time elapsed since last update.
     * Used to interpolate moving objects' positions during rendering to fluidify movements.
     */
    public final double interpolation;

    public GameGraphics(Graphics graphics, Point origin, long currentTimeTicks, double interpolation) {
        this.graphics = graphics;
        this.origin = origin;
        this.currentTimeTicks = currentTimeTicks;
        this.interpolation = interpolation;
    }
}
