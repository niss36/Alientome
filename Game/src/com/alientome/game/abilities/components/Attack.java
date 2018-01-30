package com.alientome.game.abilities.components;

import com.alientome.game.entities.Entity;

@FunctionalInterface
public interface Attack<T extends Entity> {

    void on(Entity from, T target);
}
