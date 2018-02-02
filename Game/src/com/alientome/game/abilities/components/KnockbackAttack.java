package com.alientome.game.abilities.components;

import com.alientome.core.util.Direction;
import com.alientome.game.entities.Entity;

import java.util.function.Function;

public class KnockbackAttack<T extends Entity> implements Attack<T> {

    private final Attack<? super T> base;
    private final Function<Entity, Direction> knockbackDirection;
    private final double knockbackX;
    private final double knockbackY;

    public KnockbackAttack(Attack<? super T> base, Function<Entity, Direction> knockbackDirection, double knockbackX, double knockbackY) {

        this.base = base;
        this.knockbackDirection = knockbackDirection;
        this.knockbackX = knockbackX;
        this.knockbackY = knockbackY;
    }

    @Override
    public void on(Entity from, T target) {
        base.on(from, target);
        Direction knockback = knockbackDirection.apply(from);
        target.getVelocity().add(knockbackX * knockback.normal.getX(), -knockbackY);
    }
}
