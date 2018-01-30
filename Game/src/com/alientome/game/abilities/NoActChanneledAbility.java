package com.alientome.game.abilities;

import com.alientome.game.entities.Entity;

public abstract class NoActChanneledAbility extends ChanneledAbility {

    protected NoActChanneledAbility(Entity owner, int cooldown, int maxChannelTime) {
        super(owner, cooldown, maxChannelTime, -1);
    }

    @Override
    protected void act() {
    }
}
