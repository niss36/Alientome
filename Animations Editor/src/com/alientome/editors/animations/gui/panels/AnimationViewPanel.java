package com.alientome.editors.animations.gui.panels;

import com.alientome.core.util.Direction;
import com.alientome.editors.animations.ExtAnimation;
import com.alientome.editors.animations.util.PlayState;
import com.alientome.editors.animations.util.TextAlign;
import com.alientome.editors.animations.util.Util;

import javax.swing.*;
import java.awt.*;

public class AnimationViewPanel extends JPanel implements Runnable {

    private ExtAnimation animation;
    private PlayState state = PlayState.PLAYING;
    private Direction facing = Direction.LEFT;
    private int scale = 1;
    private boolean drawOutline = false;

    public AnimationViewPanel() {
        new Thread(this).start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (canDraw()) {

            Point p = animation.getCenter(getSize(), scale);

            animation.draw(g, p.x, p.y, facing, scale, drawOutline);

            Util.drawString(g, "Frame " + (animation.getCurrentFrame() + 1) + " out of " + animation.getLength(), TextAlign.LEFT, TextAlign.BOTTOM);

            Util.drawString(g, "Zoom : " + (scale * 100) + "%", TextAlign.RIGHT, TextAlign.BOTTOM);
        }
    }

    @Override
    public void run() {

        //noinspection InfiniteLoopStatement
        while (true) {

            try {
                Thread.sleep(30);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (canDraw()) {
                if (state == PlayState.PLAYING)
                    animation.update();
                repaint();
            }
        }
    }

    public void setAnimation(ExtAnimation animation) {

        this.animation = animation;

        if (canDraw()) animation.reset();
    }

    public void setState(PlayState state) {
        this.state = state;
    }

    public void setFacing(Direction facing) {
        this.facing = facing;
    }

    public void setScale(int scale) {
        this.scale = scale;
    }

    public void setDrawOutline(boolean drawOutline) {
        this.drawOutline = drawOutline;
    }

    public void nextFrame() {
        if (canDraw()) animation.nextFrame();
    }

    public void previousFrame() {
        if (canDraw()) animation.previousFrame();
    }

    public ExtAnimation getCurrentAnimation() {
        return animation;
    }

    private boolean canDraw() {
        return animation != null;
    }
}
