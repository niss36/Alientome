package com.alientome.game.abilities.components;

import com.alientome.game.entities.Entity;
import com.alientome.visual.animations.AnimationsHandler;

public class SimpleAttackVisuals implements AttackVisuals {

    private final AnimationsHandler handler;
    private final int attack;
    private final int regular;

    public SimpleAttackVisuals(AnimationsHandler handler, int attack, int regular) {
        this.handler = handler;
        this.attack = attack;
        this.regular = regular;
    }

    @Override
    public void onStartAttacking() {
        handler.setAnimationUsed(attack);
    }

    @Override
    public void onStopAttacking() {
        handler.setAnimationUsed(regular);
    }

    @Override
    public void onAttack(Entity entity) {
    }
}
