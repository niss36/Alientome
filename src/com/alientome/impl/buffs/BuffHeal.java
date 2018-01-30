package com.alientome.impl.buffs;

import com.alientome.core.util.Vec2;
import com.alientome.game.buffs.Buff;
import com.alientome.game.entities.Entity;
import com.alientome.game.entities.EntityPlayer;

import java.awt.*;

public class BuffHeal extends Buff {

    private final int healAmount;

    /**
     * Initialize the <code>Buff</code>.
     *
     * @param pos        this <code>Buff</code>'s position
     * @param healAmount the amount this <code>Buff</code> heals
     */
    public BuffHeal(Vec2 pos, int healAmount) {
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
