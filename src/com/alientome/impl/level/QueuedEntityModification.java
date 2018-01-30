package com.alientome.impl.level;

import com.alientome.game.entities.Entity;

import java.util.List;

public class QueuedEntityModification {

    private final Entity entity;
    private final Type type;

    public QueuedEntityModification(Entity entity, Type type) {
        this.entity = entity;
        this.type = type;
    }

    public void doModification(List<Entity> entities) {

        switch (type) {

            case ADD:
                entities.add(entity);
                break;

            case REMOVE:
                entities.remove(entity);
                break;
        }
    }

    public enum Type {
        ADD,
        REMOVE
    }
}
