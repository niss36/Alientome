package com.alientome.impl.entities;

import com.alientome.core.vecmath.Vec2;
import com.alientome.game.GameObject;
import com.alientome.game.actions.Action;
import com.alientome.game.ai.AI;
import com.alientome.game.control.Control;
import com.alientome.game.control.Controller;
import com.alientome.game.entities.Entity;
import com.alientome.game.entities.EntityEnemy;
import com.alientome.game.entities.EntityGhostBall;
import com.alientome.game.entities.EntityLiving;
import com.alientome.game.level.Level;
import com.alientome.game.particles.ParticleGenerator;
import com.alientome.game.util.EntityTags;
import com.alientome.impl.actions.*;
import com.alientome.impl.ai.*;
import com.alientome.impl.particles.ParticleHeal;

import java.awt.*;

public class EntityEnemyWizard extends EntityEnemy {

    private Action[] actions;

    public EntityEnemyWizard(Vec2 pos, Level level, EntityTags tags) {

        super(pos, new Dimension(19, 28), level, tags, 10);
    }

    @Override
    protected AI createAI(EntityTags tags) {

        if (actions == null)
            actions = createActions();

        Entity player = level.getPlayer();

        int fleeRange = tags.getAsInt("fleeRange", 150);
        int healCountMin = tags.getAsInt("healCountMin", 2);
        int healRange = tags.getAsInt("healRange", 300);
        int shieldThreshold = tags.getAsInt("shieldThreshold", 5);

        AITest[] tests = {new AISeeEntity(this, player, fleeRange), new AIHasAllies(this, EntityEnemy.class, healCountMin, healRange), new AIHealth(this, shieldThreshold, false)};
        AI[] actions = {new AIFlee(this, player, fleeRange), new AIDoAction(this, this.actions[0]), new AIDoAction(this, this.actions[1])};

        return new AIRepeat(
                new AITestAction(
                        tests,
                        actions,
                        new AIWander(this)
                )
        );
    }

    private Action[] createActions() {

        ParticleGenerator generator = new ParticleGenerator((pos, velocity, time) -> new ParticleHeal(pos, level, velocity, time));

        Action heal = new Action() {
            @Override
            public void act(GameObject object) {
                if (object instanceof EntityLiving) {
                    EntityLiving entity = (EntityLiving) object;
                    entity.heal(5);
                    for (int i = 0; i < 4; i++)
                        level.spawnParticle(generator.generateRectAway(entity.getPos(), entity.dimension, 10, (i + 1) * 5));
                }
            }

            @Override
            public Level getLevel() {
                return level;
            }
        };

        Action[] actions = new Action[3];
        actions[0] =
                new ActionCoolDown(
                        new ActionAnimatedCast(
                                new ActionAreaOfEffect(
                                        new ActionSelfCast(
                                                heal,
                                                this),
                                        EntityEnemy.class, 300),
                                21, handler, 2),
                        66);
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

    @Override
    public String getNameKey() {
        return "entities.enemyWizard.name";
    }
}
