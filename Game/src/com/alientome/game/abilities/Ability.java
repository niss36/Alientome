package com.alientome.game.abilities;

import com.alientome.game.entities.Entity;

public abstract class Ability {

    protected final Entity owner;

    protected Ability(Entity owner) {
        this.owner = owner;
    }

    public abstract void update();
}
