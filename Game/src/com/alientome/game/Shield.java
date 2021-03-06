package com.alientome.game;

import com.alientome.game.entities.EntityLiving;
import com.alientome.game.entities.bars.StatusValue;

public class Shield implements StatusValue {

    private final EntityLiving owner;
    private final float maxValue;
    private final boolean blockProjectiles;
    private final boolean extendsHealth;
    private float value;

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
