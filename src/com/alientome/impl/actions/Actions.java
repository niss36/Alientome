package com.alientome.impl.actions;

import com.alientome.game.GameObject;
import com.alientome.game.actions.Action;
import com.alientome.game.entities.EntityLiving;
import com.alientome.game.level.Level;

public class Actions {

    public static Action createHeal(Level level, float amount) {

        return new Action() {
            @Override
            public void act(GameObject object) {
                if (object instanceof EntityLiving)
                    ((EntityLiving) object).heal(amount);
            }

            @Override
            public Level getLevel() {
                return level;
            }
        };
    }

    public static Action createShield(Level level, float amount) {

        return new Action() {
            @Override
            public void act(GameObject object) {
                if (object instanceof EntityLiving)
                    ((EntityLiving) object).addShield(amount);
            }

            @Override
            public Level getLevel() {
                return level;
            }
        };
    }
}
