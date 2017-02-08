package com.game.buffs;

import com.game.entities.Entity;
import com.game.entities.EntityPlayer;
import com.util.Vec2;

import java.awt.*;

class BuffShield extends Buff {

    private final int shieldAmount;

    /**
     * Initialize the <code>Buff</code>.
     *
     * @param pos this <code>Buff</code>'s position
     * @param shieldAmount the amount this <code>Buff</code> shields
     */
    @SuppressWarnings("SameParameterValue")
    BuffShield(Vec2 pos, int shieldAmount) {
        super(pos, new Dimension(32, 48));

        this.shieldAmount = shieldAmount;
    }

    @Override
    public void onEntityEntered(Entity entity) {
        if (entity instanceof EntityPlayer) {
            ((EntityPlayer) entity).addShield(shieldAmount);
        }
    }
}
