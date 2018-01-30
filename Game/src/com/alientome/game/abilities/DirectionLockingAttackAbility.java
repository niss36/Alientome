package com.alientome.game.abilities;

import com.alientome.core.util.Direction;
import com.alientome.game.abilities.components.Attack;
import com.alientome.game.abilities.components.AttackVisuals;
import com.alientome.game.abilities.components.Target;
import com.alientome.game.entities.Entity;

public class DirectionLockingAttackAbility<T extends Entity> extends AttackAbility<T> {

    private Direction attackDirection;

    public DirectionLockingAttackAbility(Entity owner, int cooldown,
                                         int attackFrames, int attackDelay,
                                         Target<T> target, Attack<T> attack, AttackVisuals visuals) {
        super(owner, cooldown, attackFrames, attackDelay, target, attack, visuals);
    }

    @Override
    protected void onStartChannel() {
        attackDirection = owner.facing;
    }

    @Override
    protected void onChannelProgress(int currentState) {
        owner.facing = attackDirection;
    }
}
