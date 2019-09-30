package com.alientome.editors.animations.io;

import com.alientome.visual.animations.io.AnimationWriter;

import java.io.*;

public class ExtAnimationWriter extends AnimationWriter {

    public ExtAnimationWriter(File target) throws IOException {

        super(new BufferedOutputStream(new FileOutputStream(target)));
    }
}
