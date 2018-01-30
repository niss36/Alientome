package com.alientome.game.abilities;

import com.alientome.game.abilities.components.Attack;
import com.alientome.game.abilities.components.AttackVisuals;
import com.alientome.game.abilities.components.Target;
import com.alientome.game.entities.Entity;

public abstract class AttackAbility<T extends Entity> extends ChanneledAbility {

    private final Target<T> target;
    private final Attack<T> attack;
    private final AttackVisuals visuals;

    public AttackAbility(Entity owner, int cooldown,
                         int attackFrames, int attackDelay,
                         Target<T> target, Attack<T> attack, AttackVisuals visuals) {
        super(owner, cooldown, attackFrames, attackDelay);
        this.target = target;
        this.attack = attack;
        this.visuals = visuals;
    }

    @Override
    public void startChannel() {
        if (isOffCooldown()) {
            super.startChannel();
            setOnCooldown();
            onStartChannel();
            visuals.onStartAttacking();
        }
    }

    @Override
    public void stopChannel() {
    }

    @Override
    protected void endChannel() {
        super.endChannel();
        visuals.onStopAttacking();
    }

    @Override
    protected void onMaxChannel() {
        endChannel();
    }

    @Override
    protected void act() {

        target.forEachTarget(t -> {
            attack.on(owner, t);
            visuals.onAttack(t);
        });
    }

    protected abstract void onStartChannel();
}
