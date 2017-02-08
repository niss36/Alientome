package com.game.buffs;

import com.game.entities.Entity;
import com.game.entities.EntityPlayer;
import com.util.Vec2;

import java.awt.*;

class BuffHeal extends Buff {

    private final int healAmount;

    /**
     * Initialize the <code>Buff</code>.
     *
     * @param pos this <code>Buff</code>'s position
     * @param healAmount the amount this <code>Buff</code> heals
     */
    @SuppressWarnings("SameParameterValue")
    BuffHeal(Vec2 pos, int healAmount) {
        super(pos, new Dimension(32, 41));

        this.healAmount = healAmount;
    }

    @Override
    public void onEntityEntered(Entity entity) {
        if (entity instanceof EntityPlayer) {
            ((EntityPlayer) entity).heal(healAmount);
        }
    }
}
