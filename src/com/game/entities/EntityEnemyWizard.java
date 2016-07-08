package com.game.entities;

import com.game.GameObject;
import com.game.entities.actions.*;
import com.game.entities.ai.*;
import com.game.level.Level;

import java.awt.*;

class EntityEnemyWizard extends EntityEnemy {

    /*private int healState = 0;
    private int coolDown = 0;*/

    @SuppressWarnings("SameParameterValue")
    EntityEnemyWizard(int x, int y, Level level, int followRange) {
        super(x, y, level, new Dimension(19, 28), followRange, false);
    }

    /*@Override
    protected AnimationInfo[] createAnimationInfo() {
        AnimationInfo[] info = new AnimationInfo[3];
        info[0] = new AnimationInfo("Entity/EnemyWizard", 10, 5);
        info[2] = new AnimationInfo("Entity/EnemyWizard/Attack", 6, 7);
        info[1] = new AnimationInfo("Entity/EnemyWizard/Heal", 6, 7);

        return info;
    }*/

    @Override
    AI createAI() {

        AITest[] tests = {new AISeeEntity(this, level.player, 150), new AIHasAllies(this, EntityEnemy.class, 2, 300)};
        AI[] actions = {new AIFlee(this, level.player, 150), new AIDoAction(this, this.actions[0])};

        return new AIRepeat(
                new AITestAction(
                        tests,
                        actions,
                        new AIWander(this)
                )
        );
    }

    @Override
    Action[] createActions() {
        Action[] actions = new Action[2];
        actions[0] =
                new ActionCoolDown(
                        new ActionAnimatedCast(
                                new ActionAreaOfEffect(
                                        new ActionSelfCast(
                                                Actions.createHeal(5),
                                                this),
                                        EntityEnemy.class, 300),
                                27, handler, 2),
                        66);

                /*new ActionAreaOfEffect(
                        new ActionSelfCast(
                                new ActionAnimation(
                                        Actions.createCoolDownCastTime(Actions.createHeal(5), 66, 27),
                                        handler, 1, 0),
                                this),
                        EntityEnemy.class, 300);*/

                /*new ActionAreaOfEffect(
                new ActionSelfCast(
                        new Action() {

                            @Override
                            public boolean shouldAct() {

                                if (coolDown > 0) coolDown--;

                                if (coolDown == 0) {

                                    handler.setAnimationUsed(1);

                                    if (healState == 27) {

                                        healState = 0;
                                        coolDown = 33;

                                        handler.setAnimationUsed(0);
                                        return true;
                                    }

                                    healState++;
                                }

                                return false;
                            }

                            @Override
                            public void act(GameObject object) {
                                ((EntityLiving) object).heal(5);
                            }
                        },
                        this),
                EntityEnemy.class, 300);*/

        actions[1] = new ActionTargeted(new Action() {
            @Override
            public boolean shouldAct() {
                return false;
            }

            @Override
            public void act(GameObject object) {
                ((EntityLiving) object).addShield(10);
            }
        }, this);

        return actions;
    }
}
