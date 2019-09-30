package com.alientome.editors.animations.io;

import com.alientome.editors.animations.ExtAnimation;
import com.alientome.visual.animations.Animation;
import com.alientome.visual.animations.io.AnimationReader;

import java.awt.Dimension;
import java.io.*;

public class ExtAnimationReader extends AnimationReader {

    private final File source;

    public ExtAnimationReader(File source) throws IOException {

        super(new BufferedInputStream(new FileInputStream(source)));

        this.source = source;
    }

    public ExtAnimation readAnimation(Dimension dimension, int scale) throws IOException {

        Animation animation = readAnimation(1);

        return new ExtAnimation(source, animation, dimension, scale);
    }
}
