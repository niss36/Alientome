package com.alientome.editors.animations.dto;

import java.awt.*;

public class AnimationDTO {

    public final String packageName;
    public final String className;
    public final String animationName;
    public final int length;
    public final int delay;
    public final boolean loop;
    public final Dimension dimension;
    public final int scale;

    public AnimationDTO(String packageName, String className, String animationName, int length, int delay, boolean loop, Dimension dimension, int scale) {
        this.packageName = packageName;
        this.className = className;
        this.animationName = animationName;
        this.length = length;
        this.delay = delay;
        this.loop = loop;
        this.dimension = dimension;
        this.scale = scale;
    }

    @Override
    public String toString() {
        return "Animation[" + packageName + ":" + className + ":" + animationName + "/" + length + "/" + delay + "/" + loop + "/" + dimension + "/" + scale + "]";
    }
}
