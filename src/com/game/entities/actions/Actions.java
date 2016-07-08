package com.game.entities.actions;

import com.game.entities.EntityLiving;

public final class Actions {

    public static Action createHeal(float amount) {

        return object -> {
            if (object instanceof EntityLiving)
                ((EntityLiving) object).heal(amount);
        };
    }

    public static Action createCoolDownCastTime(Action action, int coolDown, int castTime) {

        if (castTime > 0) {

            action = new ActionCastTime(action, castTime);
        }

        if (coolDown > 0) {

            action = new ActionCoolDown(action, coolDown);
        }

        return action;
    }
}
