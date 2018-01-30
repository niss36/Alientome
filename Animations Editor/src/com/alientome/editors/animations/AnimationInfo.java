package com.alientome.editors.animations;

import java.awt.*;
import java.io.File;

public class AnimationInfo {

    private final File source;
    private int delay;
    private int[] xOffsets;
    private int[] yOffsets;
    private boolean loop;
    private Dimension dimension;
    private int scale;

    public AnimationInfo(File source, int delay, int[] xOffsets, int[] yOffsets, boolean loop, Dimension dimension, int scale) {

        this.source = source;
        this.delay = delay;
        this.xOffsets = xOffsets;
        this.yOffsets = yOffsets;
        this.loop = loop;
        this.dimension = dimension;
        this.scale = scale;
    }

    public File getSource() {
        return source;
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public int[] getXOffsets() {
        return xOffsets;
    }

    public void setXOffsets(int[] xOffsets) {
        this.xOffsets = xOffsets;
    }

    public int[] getYOffsets() {
        return yOffsets;
    }

    public void setYOffsets(int[] yOffsets) {
        this.yOffsets = yOffsets;
    }

    public boolean isLoop() {
        return loop;
    }

    public void setLoop(boolean loop) {
        this.loop = loop;
    }

    public Dimension getDimension() {
        return dimension;
    }

    public void setDimension(Dimension dimension) {
        this.dimension = dimension;
    }

    public int getScale() {
        return scale;
    }

    public void setScale(int scale) {
        this.scale = scale;
    }
}
