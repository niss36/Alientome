package com.alientome.game.abilities.components;

import com.alientome.game.entities.Entity;

public interface AttackVisuals {

    void onStartAttacking();

    void onStopAttacking();

    void onAttack(Entity entity);
}
