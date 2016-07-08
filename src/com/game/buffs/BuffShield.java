package com.game.buffs;

import com.game.entities.Entity;
import com.game.entities.EntityPlayer;
import com.game.level.Level;

import java.awt.*;

public class BuffShield extends Buff {

    private final int shieldAmount;

    /**
     * Initialize the <code>Buff</code>.
     *
     * @param x     the x coordinate
     * @param y     the y coordinate
     * @param level the <code>Level</code> this <code>Buff</code> is in
     */
    @SuppressWarnings("SameParameterValue")
    BuffShield(int x, int y, Level level, int shieldAmount) {
        super(x, y, new Dimension(32, 48), level);

        this.shieldAmount = shieldAmount;
    }

    /*@Override
    AnimationInfo[] createAnimationInfo() {
        return new AnimationInfo[1];
    }*/

    @Override
    public void entityEntered(Entity entity) {

        if (entity instanceof EntityPlayer) {
            ((EntityPlayer) entity).addShield(shieldAmount);
        }
    }
}
