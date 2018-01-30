package com.alientome.game.abilities.components;

import com.alientome.game.entities.Entity;
import com.alientome.game.entities.EntityLiving;

public class SimpleAttack<T extends EntityLiving> implements Attack<T> {

    private final float damage;

    public SimpleAttack(float damage) {
        this.damage = damage;
    }

    @Override
    public void on(Entity from, T target) {
        target.damage(damage);
    }
}
