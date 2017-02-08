package com.game.entities;

import com.game.control.Control;
import com.game.control.Controller;
import com.game.entities.ai.*;
import com.game.level.Level;
import com.util.Vec2;

import java.awt.*;

public class EntityEnemyBow extends EntityEnemy {

    private int coolDown = 0;
    private int fireState = -1;

    /**
     * @param pos this <code>Entity</code>'s position
     * @param level the <code>Level</code> this <code>Entity</code> is in
     */
    @SuppressWarnings("SameParameterValue")
    EntityEnemyBow(Vec2 pos, Level level) {
        super(pos, new Dimension(19, 27), level, 10, 0, false);

        maxVelocity = 4;
    }

    public void startCharging() {

        if (coolDown == 0 && fireState < 0) fireState = 0;
    }

    public void stopCharging() {

        if (fireState >= 0) {

            fire();
            fireState = -1;
        }
    }

    public void fire() {

        if (coolDown == 0) {

            float chargeRatio = fireState / 17f;

            level.spawnEntity(new EntityArrow(this, chargeRatio * 3, chargeRatio * (-level.getPlayer().distanceTo(this) + entityRandom.nextInt(50) - 25) / 25));
            coolDown = 33;
        }
    }

    @Override
    void preUpdateInternal() {

        if (coolDown > 0) coolDown--;

        if (fireState >= 0) {
            if (fireState >= 17) stopCharging();
            else {
                handler.setAnimationUsed(1);
                fireState++;
            }
        } else
            handler.setAnimationUsed(0);

        super.preUpdateInternal();
    }

    @Override
    AI createAI() {

        Entity player = level.getPlayer();

        AITest[] tests = {new AISeeEntity(this, player, 150), new AISeeEntity(this, player, 400)};
        AI[] actions = {new AIFlee(this, player, 150), new AIFire(this, player)};

        return new AIRepeat(
                new AITestAction(
                        tests,
                        actions,
                        new AIWander(this)
                )
        );
    }

    @Override
    public Controller newController() {
        Controller controller = super.newController();
        controller.addControl(Control.createChargeControl(this, "special1",
                entity -> ((EntityEnemyBow) entity).startCharging(),
                entity -> ((EntityEnemyBow) entity).stopCharging()));

        return controller;
    }
}
