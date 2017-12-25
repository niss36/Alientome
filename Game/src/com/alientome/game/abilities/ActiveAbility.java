package com.alientome.game.abilities;

import com.alientome.game.entities.Entity;
import com.alientome.game.entities.bars.StatusValue;

public abstract class ActiveAbility extends Ability {

    private final int cooldown;
    private int currentCooldown;

    protected ActiveAbility(Entity owner, int cooldown) {
        super(owner);
        this.cooldown = cooldown;
    }

    protected void setOnCooldown() {
        currentCooldown = cooldown;
    }

    protected boolean isOffCooldown() {
        return currentCooldown == 0;
    }

    public StatusValue getCooldownValue() {
        return new StatusValue() {
            @Override
            public float percentValue() {
                return (float) currentCooldown / cooldown;
            }

            @Override
            public float percentValue(double interpolation) {
                return (float) (Math.max(currentCooldown - interpolation, 0)) / cooldown;
            }
        };
    }

    @Override
    public void update() {
        if (currentCooldown > 0) currentCooldown--;
    }
}
