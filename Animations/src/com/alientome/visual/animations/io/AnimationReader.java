package com.alientome.visual.animations.io;

import com.alientome.core.util.Util;
import com.alientome.visual.animations.Animation;
import com.alientome.visual.animations.AnimationImpl;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.Closeable;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * An AnimationReader wraps around an InputStream in order to read Alientome Animation files.
 */
public class AnimationReader implements Closeable {

    /**
     * The InputStream we will be gathering data from.
     */
    protected final DataInputStream stream;

    public AnimationReader(InputStream stream) {
        this(new DataInputStream(stream));
    }

    public AnimationReader(DataInputStream stream) {
        this.stream = stream;
    }

    /**
     * Reads an animation from the underlying InputStream.
     * The stream is not closed after this operation.
     *
     * @param scale images scaling rate : a scale of 2 means we should read images twice as big. Cannot be < 1.
     * @return A new Animation object
     * @throws IOException              if an I/O Error occurs
     * @throws IllegalArgumentException if the scale is < 1
     */
    public Animation readAnimation(int scale) throws IOException {

        if (scale < 1)
            throw new IllegalArgumentException("Illegal scale argument (" + scale + ") : Cannot be < 1");

        //Number of images
        int length = stream.readInt();
        //Delay between said images
        int delay = stream.readInt();
        //Whether the animation should loop indefinitely
        boolean loop = stream.readBoolean();

        BufferedImage[] sprites = new BufferedImage[length];
        //The x value to be added when drawing the sprite
        int[] xOffsets = new int[length];
        //The y value to be added when drawing the sprite
        int[] yOffsets = new int[length];

        //Do all x offsets share the same value ? (Minor optimization technique)
        boolean xOffsetsUniform = stream.readBoolean();
        if (xOffsetsUniform)
            xOffsets[0] = stream.readInt() * scale;

        //Do all y offsets share the same value ?
        boolean yOffsetsUniform = stream.readBoolean();
        if (yOffsetsUniform)
            yOffsets[0] = stream.readInt() * scale;

        for (int i = 0; i < length; i++) {

            BufferedImage image = ImageIO.read(stream);

            if (scale == 1) //That means we have nothing more to do, simply put the image in array.
                sprites[i] = image;
            else //We have to upscale the image.
                sprites[i] = Util.scale(image, scale);

            //noinspection StatementWithEmptyBody
            while (stream.readByte() != (byte) 0xFF) ;
            //Make sure we're past the image data (0xFF is a marker byte, see AnimationWriter)

            //Iteratively fill up the arrays
            if (xOffsetsUniform) //From the saved value
                xOffsets[i] = xOffsets[0];
            else //Or from a read value
                xOffsets[i] = stream.readInt() * scale;

            if (yOffsetsUniform)
                yOffsets[i] = yOffsets[0];
            else
                yOffsets[i] = stream.readInt() * scale;
        }

        return new AnimationImpl(sprites, delay, xOffsets, yOffsets, loop);
    }

    @Override
    public void close() throws IOException {
        stream.close();
    }
}
