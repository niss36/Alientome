package com.alientome.editors.animations.io;

import com.alientome.editors.animations.Animation;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

public class AnimationReader implements Closeable {

    private final File source;
    private final DataInputStream stream;

    public AnimationReader(File source) throws IOException {

        this.source = source;
        stream = new DataInputStream(new BufferedInputStream(new FileInputStream(source)));
    }

    public Animation readAnimation(Dimension dimension, int scale) throws IOException {

        int length = stream.readInt();
        int delay = stream.readInt();
        boolean loop = stream.readBoolean();

        BufferedImage[] sprites = new BufferedImage[length];
        int[] xOffsets = new int[length];
        int[] yOffsets = new int[length];

        boolean xOffsetsUniform = stream.readBoolean();
        if (xOffsetsUniform)
            xOffsets[0] = stream.readInt();

        boolean yOffsetsUniform = stream.readBoolean();
        if (yOffsetsUniform)
            yOffsets[0] = stream.readInt();

        for (int i = 0; i < length; i++) {

            sprites[i] = ImageIO.read(stream);

            //noinspection StatementWithEmptyBody
            while (stream.readByte() != (byte) 0xFF) ;

            if (xOffsetsUniform)
                xOffsets[i] = xOffsets[0];
            else
                xOffsets[i] = stream.readInt();

            if (yOffsetsUniform)
                yOffsets[i] = yOffsets[0];
            else
                yOffsets[i] = stream.readInt();
        }

        return new Animation(sprites, source, delay, xOffsets, yOffsets, loop, dimension, scale);
    }

    @Override
    public void close() throws IOException {
        stream.close();
    }
}
