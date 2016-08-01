package com.game.buffs;

import com.game.entities.Entity;
import com.game.entities.EntityPlayer;

import java.awt.*;

class BuffShield extends Buff {

    private final int shieldAmount;

    /**
     * Initialize the <code>Buff</code>.
     *
     * @param x     the x coordinate
     * @param y     the y coordinate
     */
    @SuppressWarnings("SameParameterValue")
    BuffShield(int x, int y, int shieldAmount) {
        super(x, y, new Dimension(32, 48));

        this.shieldAmount = shieldAmount;
    }

    @Override
    public void entityEntered(Entity entity) {

        if (entity instanceof EntityPlayer) {
            ((EntityPlayer) entity).addShield(shieldAmount);
        }
    }
}
