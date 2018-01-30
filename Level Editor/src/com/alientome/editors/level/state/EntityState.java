package com.alientome.editors.level.state;

import com.alientome.core.util.WrappedXML;
import com.alientome.editors.level.Sprite;

import java.awt.*;

public class EntityState {

    public final String name;
    public final String id;
    public final Sprite sprite;

    public EntityState(String name, String id, Sprite sprite) {
        this.name = name;
        this.id = id;
        this.sprite = sprite;
    }

    public static EntityState fromXML(WrappedXML stateXML) {

        String name = stateXML.getAttr("name");
        String id = stateXML.getAttr("id");

        WrappedXML visualXML = new WrappedXML(stateXML.nodes("visual").get(0));
        Sprite sprite = Sprite.fromXML(visualXML, new Dimension());

        return new EntityState(name, id, sprite);
    }
}
