package com.game.entities.actions;

import com.game.GameObject;
import com.game.entities.EntityLiving;
import com.game.level.Level;

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
