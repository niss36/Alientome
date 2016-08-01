package com.game.buffs;

import com.game.entities.Entity;
import com.game.entities.EntityPlayer;

import java.awt.*;

class BuffHeal extends Buff {

    private final int healAmount;

    /**
     * Initialize the <code>Buff</code>.
     *
     * @param x          the x coordinate
     * @param y          the y coordinate
     * @param healAmount the amount this <code>Buff</code> heals
     */
    @SuppressWarnings("SameParameterValue")
    BuffHeal(int x, int y, int healAmount) {
        super(x, y, new Dimension(32, 41));

        this.healAmount = healAmount;
    }

    @Override
    public void entityEntered(Entity entity) {
        if (entity instanceof EntityPlayer) {
            ((EntityPlayer) entity).heal(healAmount);
        }
    }
}
