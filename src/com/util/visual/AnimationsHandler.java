package com.util.visual;

import com.game.GameObject;
import com.util.Direction;

import java.awt.*;

public class AnimationsHandler {

    private final Animation[] animations;
    private int animationUsed = 0;

    public AnimationsHandler(Class<? extends GameObject> objectClass) {

        animations = SpritesLoader.getAnimations(objectClass);
    }

    public void setAnimationUsed(int index) {

        if (animationUsed != index) {
            int temp = animationUsed;
            try {
                animationUsed = index;
                animations[animationUsed].reset();
            } catch (ArrayIndexOutOfBoundsException e) {
                animationUsed = temp;
            }
        }
    }

    public void draw(Graphics g, int x, int y, Direction facing) {
        animations[animationUsed].draw(g, x, y, facing);
    }

    public void draw(Graphics g, int x, int y) {
        animations[animationUsed].draw(g, x, y);
    }

    public boolean canDraw() {
        return animations != null && animations[animationUsed] != null;
    }
}
