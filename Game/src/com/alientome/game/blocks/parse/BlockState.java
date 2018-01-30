package com.alientome.game.blocks.parse;

public class BlockState {

    public final String identifier;
    public final byte metadata;

    public BlockState(String identifier, byte metadata) {
        this.identifier = identifier;
        this.metadata = metadata;
    }

    @Override
    public String toString() {
        return String.format("BlockState [id=%s, metadata=%d]", identifier, metadata);
    }
}
