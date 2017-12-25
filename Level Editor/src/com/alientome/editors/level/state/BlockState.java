package com.alientome.editors.level.state;

import com.alientome.core.util.WrappedXML;
import com.alientome.editors.level.Sprite;

import java.awt.*;

public class BlockState {

    public static final int WIDTH = 32;

    public final String name;
    public final String id;
    public final byte metadata;
    public final Sprite sprite;

    public BlockState(String name, String id, byte metadata, Sprite sprite) {
        this.name = name;
        this.id = id;
        this.metadata = metadata;
        this.sprite = sprite;
    }

    public static BlockState fromXML(WrappedXML stateXML) {

        String name = stateXML.getAttr("name");
        String id = stateXML.getAttr("id");
        byte meta = stateXML.getAttrByte("meta");

        WrappedXML visualXML = new WrappedXML(stateXML.nodes("visual").get(0));

        @SuppressWarnings("SuspiciousNameCombination")
        Sprite sprite = Sprite.fromXML(visualXML, new Dimension(WIDTH, WIDTH));

        return new BlockState(name, id, meta, sprite);
    }
}
