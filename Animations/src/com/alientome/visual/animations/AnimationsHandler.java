package com.alientome.visual.animations;

import com.alientome.core.graphics.GameGraphics;
import com.alientome.core.util.Direction;
import com.alientome.core.util.Logger;

/**
 * An AnimationsHandler object is responsible for doing all tasks linked with managing several Animations. That includes
 * keeping track of currently displayed animation, of the last draw timestamp, of the currently displayed image of the
 * current animation, and on enforcing delay and loop restrictions.
 */
public class AnimationsHandler {

    /**
     * Internal logging utility.
     */
    protected static final Logger log = Logger.get();

    /**
     * The array of animations.
     */
    protected final Animation[] animations;

    /**
     * The index of the animation currently displayed.
     */
    protected int animationUsed = 0;

    /**
     * The last time the animation was drawn to the screen, as a game ticks based timestamp. It is used to decide when to
     * increment the tick counter.
     */
    protected long lastDraw = 0;

    /**
     * The index of the displayed frame in current animation.
     */
    protected int imageUsed = 0;

    /**
     * A game ticks counter. Used to increment the imageUsed index when this variable reaches the current animation's delay value.
     */
    protected int count = 0;

    /**
     * A task to execute when the current animation has reached its last frame.
     */
    protected Runnable endListener;

    public AnimationsHandler(Animation[] animations) {
        this.animations = animations;
    }

    public void setAnimationUsed(int index) {

        if (index != animationUsed)
            if (index < animations.length) {
                animationUsed = index;
                reset();
            } else log.w("Incorrect animation index used : " + index + " / " + animations.length);
    }

    public void draw(GameGraphics g, int x, int y, Direction facing) {

        Animation used = animations[animationUsed];

        long inc = g.currentTimeTicks - lastDraw;

        count += inc;

        if (count >= used.getDelay()) {
            count = 0;
            imageUsed++;
        }

        if (imageUsed >= used.getLength()) {
            if (used.isLoop()) imageUsed = 0;
            else imageUsed = used.getLength() - 1;

            if (endListener != null) {
                endListener.run();
                endListener = null;
            }
        }

        lastDraw = g.currentTimeTicks;

        used.draw(g.graphics, x, y, facing, imageUsed);
    }

    public void draw(GameGraphics g, int x, int y) {
        draw(g, x, y, Direction.LEFT);
    }

    public boolean canDraw() {
        return animations != null && animations[animationUsed] != null;
    }

    public void reset() {
        imageUsed = 0;
        count = 0;
    }

    public void setAnimationEndListener(Runnable listener) {
        this.endListener = listener;
    }
}
