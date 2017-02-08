package com.game.entities;

import com.game.GameObject;
import com.game.control.Control;
import com.game.control.Controller;
import com.game.entities.actions.*;
import com.game.entities.ai.*;
import com.game.level.Level;
import com.util.Vec2;

import java.awt.*;

public class EntityEnemyWizard extends EntityEnemy {

    /*private int healState = 0;
    private int coolDown = 0;*/

    @SuppressWarnings("SameParameterValue")
    EntityEnemyWizard(Vec2 pos, Level level, int followRange) {
        super(pos, new Dimension(19, 28), level, 10, followRange, false);

        maxVelocity = 10;
    }

    @Override
    AI createAI() {

        Entity player = level.getPlayer();

        AITest[] tests = {new AISeeEntity(this, player, 150), new AIHasAllies(this, EntityEnemy.class, 2, 300), new AIHealth(this, 5, false)};
        AI[] actions = {new AIFlee(this, player, 150), new AIDoAction(this, this.actions[0]), new AIDoAction(this, this.actions[1])};

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
        Action[] actions = new Action[3];
        actions[0] =
                new ActionCoolDown(
                        new ActionAnimatedCast(
                                new ActionAreaOfEffect(
                                        new ActionSelfCast(
                                                Actions.createHeal(level, 5),
                                                this),
                                        EntityEnemy.class, 300),
                                21, handler, 2),
                        66);

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

        actions[1] =
                new ActionCoolDown(
                        new ActionAnimatedCast(
                                new ActionTargeted(
                                        Actions.createShield(level, 10),
                                        this),
                                21, handler, 1),
                        66);

        actions[2] =
                new ActionCoolDown(
                        new ActionAnimatedCast(
                                new Action() {

                                    @Override
                                    public void act() {
                                        level.spawnEntity(new EntityGhostBall(EntityEnemyWizard.this, false));
                                    }

                                    @Override
                                    public void act(GameObject object) {

                                    }

                                    @Override
                                    public Level getLevel() {
                                        return level;
                                    }
                                },
                                21, handler, 1),
                66);

        return actions;
    }

    @Override
    public Controller newController() {
        Controller controller = super.newController();
        controller.addControl(Control.createActionControl(this, "special1", actions[0]));
        controller.addControl(Control.createActionControl(this, "special2", actions[1]));
        controller.addControl(Control.createActionControl(this, "special3", actions[2]));

        return controller;
    }
}
