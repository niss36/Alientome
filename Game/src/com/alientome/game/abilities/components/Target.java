package com.alientome.game.abilities.components;

import com.alientome.game.entities.Entity;

import java.util.function.Consumer;

@FunctionalInterface
public interface Target<T extends Entity> {

    void forEachTarget(Consumer<T> action);
}
