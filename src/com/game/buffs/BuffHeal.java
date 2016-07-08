package com.game.buffs;

import com.game.entities.Entity;
import com.game.entities.EntityPlayer;
import com.game.level.Level;

import java.awt.*;

public class BuffHeal extends Buff {

    private final int healAmount;

    /**
     * Initialize the <code>Buff</code>.
     *
     * @param x          the x coordinate
     * @param y          the y coordinate
     * @param level      the <code>Level</code> this <code>Buff</code> is in
     * @param healAmount the amount this <code>Buff</code> heals
     */
    @SuppressWarnings("SameParameterValue")
    BuffHeal(int x, int y, Level level, int healAmount) {
        super(x, y, new Dimension(32, 41), level);

        this.healAmount = healAmount;
    }

    /*@Override
    AnimationInfo[] createAnimationInfo() {
        AnimationInfo[] info = new AnimationInfo[1];
        info[0] = new AnimationInfo("Buff/Heal", 8, 10);

        return info;
    }*/

    @Override
    public void entityEntered(Entity entity) {
        if (entity instanceof EntityPlayer) {
            ((EntityPlayer) entity).heal(healAmount);
        }
    }
}
