package com.game.entities;

import com.game.Level;
import com.game.entities.ai.*;
import com.util.visual.AnimationInfo;
import com.util.Direction;

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
    public EntityEnemyBow(int x, int y, Level level) {
        super(x, y, level, new Dimension(19, 27), 0, false);

        maxVelocity = 4;
    }

    public void fire() {

        if(coolDown == 0) {

            fireState = 0;
            setAnimationInUse(1);
            coolDown = 33;
        }
    }

    @Override
    public void onUpdate() {

        if(coolDown > 0) coolDown--;

        if(fireState >= 0) {
            if(fireState < 11) fireState ++;
            else {
                level.spawnEntity(new EntityArrow(this, 3, -level.player.distanceTo(this)/25));
                fireState = -1;
                setAnimationInUse(0);
            }
        }

        super.onUpdate();
    }

    @Override
    void draw(Graphics g, int x, int y) {
        if(fireState == -1 || facing == Direction.RIGHT) super.draw(g, x, y);
        else {

            int s = (fireState + 1) / 4;

            int x1 = x - (s == 1 ? 4 : s == 2 ? 2 : 0);

            super.draw(g, x1, y);

        }
    }

    @Override
    protected AnimationInfo[] createAnimationInfo() {
        AnimationInfo[] info = new AnimationInfo[2];
        info[0] = new AnimationInfo("EnemyBow", 1, 10);
        info[1] = new AnimationInfo("EnemyBow/Fire", 3, 4);

        return info;
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
