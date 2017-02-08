package com.game.blocks;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class BlockBuilder implements BlockConstants, Serializable {

    private static final Map<Integer, Byte> rgbConvert = new HashMap<>();

    static {
        rgbConvert.put(0x0094FF, AIR);
        rgbConvert.put(0xFFD800, SAND);
        rgbConvert.put(0x000000, HOLE);
    }

    public final byte index;
    public final byte metadata;

    public BlockBuilder(byte index, byte metadata) {
        this.index = index;
        this.metadata = metadata;
    }

    public static Block parseRGBA(int x, int y, int rgbaCode) {

        int rgb = rgbaCode & 0xFFFFFF;
        short alpha = (short) (rgbaCode >> 24 & 0xFF);

        BlockBuilder builder = null;

        if (rgbConvert.containsKey(rgb)) {
            byte index = rgbConvert.get(rgb);
            byte metadata = (byte) ((255 - alpha) >>> 4);
            builder = new BlockBuilder(index, metadata);
        }

        return Block.create(x, y, builder);
    }

    @Override
    public String toString() {
        return String.format("BlockBuilder[index=%d, metadata=%d]", index, metadata);
    }
}
