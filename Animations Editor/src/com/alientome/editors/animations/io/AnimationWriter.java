package com.alientome.editors.animations.io;

import com.alientome.editors.animations.Animation;
import com.alientome.editors.animations.AnimationInfo;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

public class AnimationWriter implements Closeable, Flushable {

    private final DataOutputStream stream;

    public AnimationWriter(File target) throws IOException {

        stream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(target)));
    }

    public void writeAnimation(Animation animation) throws IOException {

        AnimationInfo info = animation.getInfo();

        int length = animation.getLength();
        BufferedImage[] sprites = animation.getSprites();
        int[] xOffsets = info.getXOffsets();
        int[] yOffsets = info.getYOffsets();

        boolean xOffsetsUniform = true;
        boolean yOffsetsUniform = true;

        for (int i = 1; i < length; i++) {
            if (xOffsets[i] != xOffsets[i - 1])
                xOffsetsUniform = false;
            if (yOffsets[i] != yOffsets[i - 1])
                yOffsetsUniform = false;
        }

        stream.writeInt(length);
        stream.writeInt(info.getDelay());
        stream.writeBoolean(info.isLoop());

        stream.writeBoolean(xOffsetsUniform);
        if (xOffsetsUniform)
            stream.writeInt(xOffsets[0]);

        stream.writeBoolean(yOffsetsUniform);
        if (yOffsetsUniform)
            stream.writeInt(yOffsets[0]);

        for (int i = 0; i < length; i++) {

            ImageIO.write(sprites[i], "png", stream);

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
