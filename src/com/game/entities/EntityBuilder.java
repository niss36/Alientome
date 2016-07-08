package com.game.entities;

import com.game.level.Level;
import org.w3c.dom.Element;

public class EntityBuilder {

    private final int type;
    private final int x;
    private final int y;
    private final Level level;

    private EntityBuilder(int type, int x, int y, Level level) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.level = level;
    }

    public static EntityBuilder parse(Element entityNode, Level level) {

        int type = Integer.parseInt(entityNode.getAttribute("type"));
        int x = Integer.parseInt(entityNode.getAttribute("spawnX"));
        int y = Integer.parseInt(entityNode.getAttribute("spawnY"));

        return new EntityBuilder(type, x, y, level);
    }

    public Entity create() {
        return Entity.createFromBlockPos(type, x, y, level);
    }
}
