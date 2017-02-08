package com.game;

import com.game.entities.EntityLiving;

public class Shield {

    private final EntityLiving owner;
    private final float maxValue;
    private final boolean blockProjectiles;
    private final boolean extendsHealth;
    private float value;

    @SuppressWarnings("SameParameterValue")
    public Shield(EntityLiving owner, float value, boolean blockProjectiles, boolean extendsHealth) {

        this.owner = owner;
        this.value = maxValue = value;
        this.blockProjectiles = blockProjectiles;
        this.extendsHealth = extendsHealth;
    }

    public void damage(float value, boolean hitByProjectile) {

        if (this.value <= 0) owner.damageAbsolute(value);

        if (blockProjectiles) {
            if (hitByProjectile) this.value -= value;
            else owner.damageAbsolute(value);
        } else this.value -= value;

        if (this.value < 0 && extendsHealth) owner.damageAbsolute(-this.value);
    }

    public float percentValue() {
        return value / maxValue;
    }
}
