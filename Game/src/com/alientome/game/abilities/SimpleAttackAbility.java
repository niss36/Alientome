package com.alientome.game.abilities;

import com.alientome.game.abilities.components.Attack;
import com.alientome.game.abilities.components.AttackVisuals;
import com.alientome.game.abilities.components.Target;
import com.alientome.game.entities.Entity;

public class SimpleAttackAbility<T extends Entity> extends AttackAbility<T> {

    public SimpleAttackAbility(Entity owner, int cooldown,
                               int attackFrames, int attackDelay,
                               Target<T> target, Attack<T> attack, AttackVisuals visuals) {
        super(owner, cooldown, attackFrames, attackDelay, target, attack, visuals);
    }

    @Override
    protected void onChannelProgress(int currentState) {
    }

    @Override
    protected void onStartChannel() {
    }
}
