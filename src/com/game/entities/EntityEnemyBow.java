package com.game.entities;

import com.game.entities.ai.*;
import com.game.level.Level;

import java.awt.*;

public class EntityEnemyBow extends EntityEnemy {

    private int coolDown = 0;
    private int fireState = -1;

    /**
     * @param x     the x coordinate
     * @param y     the y coordinate
     * @param level the <code>Level</code> this <code>Entity</code> is in
     */
    @SuppressWarnings("SameParameterValue")
    EntityEnemyBow(int x, int y, Level level) {
        super(x, y, level, new Dimension(19, 27), 0, false);

        maxVelocity = 4;
    }

    public void fire() {

        if (coolDown == 0) {

            fireState = 0;

            handler.setAnimationUsed(1);
            coolDown = 33;
        }
    }

    @Override
    public void onUpdate() {

        if (coolDown > 0) coolDown--;

        if (fireState >= 0) {
            if (fireState < 17) fireState++;
            else {
                level.spawnEntity(new EntityArrow(this, 3, (-level.player.distanceTo(this) + entityRandom.nextInt(50) - 25) / 25));
                fireState = -1;

                handler.setAnimationUsed(0);
            }
        }

        super.onUpdate();
    }

    @Override
    AI createAI() {

        AITest[] tests = {new AISeeEntity(this, level.player, 150), new AISeeEntity(this, level.player, 400)};
        AI[] actions = {new AIFlee(this, level.player, 150), new AIFire(this, level.player)};

        return new AIRepeat(
                new AITestAction(
                        tests,
                        actions,
                        new AIWander(this)
                )
        );
    }
}
