package com.util.visual;

import com.game.GameObject;
import com.util.Direction;
import com.util.Logger;

public class AnimationsHandler {

    private static final Logger log = Logger.get();

    private final Animation[] animations;
    private int animationUsed = 0;

    private long lastUpdate = 0;

    private int imageUsed = 0;
    private int count = 0;
    private Runnable listener;

    public AnimationsHandler(Class<? extends GameObject> objectClass) {

        animations = SpritesLoader.getAnimations(objectClass);
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

        long inc = g.currentTimeTicks - lastUpdate;

        count += inc;

        if (count >= used.getDelay()) {
            count = 0;
            imageUsed++;
        }

        if (imageUsed >= used.getLength()) {
            if (used.isLoop()) imageUsed = 0;
            else imageUsed = used.getLength() - 1;

            if (listener != null) {
                listener.run();
                listener = null;
            }
        }

        lastUpdate = g.currentTimeTicks;

        used.draw(g, x, y, facing, imageUsed);
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
        this.listener = listener;
    }
}
