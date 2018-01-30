package com.alientome.visual.animations.io;

import com.alientome.visual.animations.Animation;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

/**
 * An AnimationWriter wraps around an OutputStream in order to write / save Alientome Animation files.
 */
public class AnimationWriter implements Closeable, Flushable {

    /**
     * The OutputStream we will be writing to.
     */
    protected final DataOutputStream stream;

    public AnimationWriter(OutputStream stream) {
        this(new DataOutputStream(stream));
    }

    public AnimationWriter(DataOutputStream stream) {
        this.stream = stream;
    }

    /**
     * Writes an Animation to the underlying OutputStream.
     * The stream is not closed after this operation.
     *
     * @param animation the Animation to be written
     * @throws IOException if an I/O Error occurs
     */
    public void writeAnimation(Animation animation) throws IOException {

        //The number of images
        int length = animation.getLength();

        BufferedImage[] sprites = animation.getSprites();
        int[] xOffsets = animation.getXOffsets();
        int[] yOffsets = animation.getYOffsets();

        //Do all x / y offsets share one same value ?
        boolean xOffsetsUniform = true;
        boolean yOffsetsUniform = true;

        for (int i = 1; i < length; i++) {

            if (xOffsets[i] != xOffsets[i - 1]) //At least one value differs
                xOffsetsUniform = false;
            if (yOffsets[i] != yOffsets[i - 1])
                yOffsetsUniform = false;
        }

        stream.writeInt(length);
        stream.writeInt(animation.getDelay());
        stream.writeBoolean(animation.isLoop());

        stream.writeBoolean(xOffsetsUniform);
        if (xOffsetsUniform)
            stream.writeInt(xOffsets[0]); //Only write the first offset, as all are equivalent

        stream.writeBoolean(yOffsetsUniform);
        if (yOffsetsUniform)
            stream.writeInt(yOffsets[0]);

        for (int i = 0; i < length; i++) {

            ImageIO.write(sprites[i], "png", stream);

            //Places a marker byte right after the image data, in order to make sure to read past it. See AnimationReader
            stream.writeByte(0xFF);

            if (!xOffsetsUniform)
                stream.writeInt(xOffsets[i]);
            if (!yOffsetsUniform)
                stream.writeInt(yOffsets[i]);
        }
    }

    @Override
    public void close() throws IOException {
        stream.close();
    }

    @Override
    public void flush() throws IOException {
        stream.flush();
    }
}
