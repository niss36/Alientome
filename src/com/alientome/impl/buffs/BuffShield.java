package com.alientome.impl.buffs;

import com.alientome.core.util.Vec2;
import com.alientome.game.buffs.Buff;
import com.alientome.game.entities.Entity;
import com.alientome.game.entities.EntityPlayer;

import java.awt.*;

public class BuffShield extends Buff {

    private final int shieldAmount;

    /**
     * Initialize the <code>Buff</code>.
     *
     * @param pos          this <code>Buff</code>'s position
     * @param shieldAmount the amount this <code>Buff</code> shields
     */
    public BuffShield(Vec2 pos, int shieldAmount) {
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
