package com.alientome.game.entities;

import com.alientome.core.collisions.AxisAlignedBoundingBox;
import com.alientome.core.util.Direction;
import com.alientome.core.vecmath.Vec2;
import com.alientome.game.abilities.AttackAbility;
import com.alientome.game.abilities.ChanneledAbility;
import com.alientome.game.abilities.DirectionLockingAttackAbility;
import com.alientome.game.abilities.NoActChanneledAbility;
import com.alientome.game.abilities.components.*;
import com.alientome.game.control.Control;
import com.alientome.game.control.Controller;
import com.alientome.game.entities.bars.FillColorProvider;
import com.alientome.game.entities.bars.SimpleFillColor;
import com.alientome.game.entities.bars.StatusBar;
import com.alientome.game.level.Level;
import com.alientome.game.particles.Particle;
import com.alientome.game.particles.ParticleGenerator;
import com.alientome.game.particles.ParticlePlayerCharge;
import com.alientome.game.util.EntityTags;
import com.alientome.game.util.Util;

import java.awt.*;

import static com.alientome.core.util.Colors.*;

public class EntityPlayer extends EntityLiving {

    private final ParticleGenerator chargingGenerator =
            new ParticleGenerator((pos, velocity, time) -> new ParticlePlayerCharge(pos, level, velocity, time, 0));
    private final ParticleGenerator chargedGenerator =
            new ParticleGenerator((pos, velocity, time) -> new ParticlePlayerCharge(pos, level, velocity, time, 1));

    private final ChanneledAbility attackAbility = createAttackAbility();
    private final ChanneledAbility ghostBallAbility = createGhostBallAbility();
    private final ChanneledAbility dashAbility = createDashAbility();

    public EntityPlayer(Vec2 pos, Level level, EntityTags tags) {

        super(pos, new Dimension(20, 31), level, tags, 20);

        statusBars.add(new StatusBar(new SimpleFillColor(COOLDOWN_BAR), null, 4, false, ghostBallAbility.getCooldownValue().reversed()));
    }

    @Override
    protected FillColorProvider getHealthFill() {
        return percentValue -> {
            if (percentValue >= 0.75) return HEALTH_75;
            if (percentValue >= 0.50) return HEALTH_50;
            return HEALTH_0;
        };
    }

    @Override
    protected void preUpdateInternal() {

        attackAbility.update();
        ghostBallAbility.update();
        dashAbility.update();
    }

    @Override
    protected void onSetDead() {
        super.onSetDead();
        controller.notifyControlledDeath();
        deathTimer = 10;
    }

    @Override
    public Controller newController() {
        Controller controller = super.newController();
        controller.addControl(Control.createContinuousControl(this, "special1",
                p -> p.attackAbility.startChannel()));
        controller.addControl(Control.createChargeControl(this, "special2",
                p -> p.ghostBallAbility.startChannel(), p -> p.ghostBallAbility.stopChannel()));
        controller.addControlOverride(Control.createDoublePressControl(this, "moveLeft", 300,
                p -> p.dashAbility.startChannel()));
        controller.addControlOverride(Control.createDoublePressControl(this, "moveRight", 300,
                p -> p.dashAbility.startChannel()));

        return controller;
    }

    @Override
    public String getNameKey() {
        return "entities.player.name";
    }

    private AttackAbility<EntityEnemy> createAttackAbility() {

        Attack<EntityEnemy> attack = new KnockbackAttack<>(
                new SimpleAttack<>(2.5f),
                entity -> entity.facing,
                3, 2);
        Target<EntityEnemy> target = action -> {

            AxisAlignedBoundingBox area = Util.getNextFrontBoundingBox(this, 20, 5);

            level.forEachCollidableEntity(area, entity -> {
                if (entity instanceof EntityEnemy)
                    action.accept((EntityEnemy) entity);
            }, this);
        };
        AttackVisuals visuals = new SimpleAttackVisuals(handler, 1, 0);

        return new DirectionLockingAttackAbility<>(this, 17, 15, 10, target, attack, visuals);
    }

    private ChanneledAbility createGhostBallAbility() {
        return new NoActChanneledAbility(this, 33, 39) {

            private boolean isMaxChannel = false;

            @Override
            public void startChannel() {
                if (isOffCooldown() && isStopped())
                    super.startChannel();
            }

            @Override
            public void stopChannel() {
                if (isOffCooldown()) {
                    level.spawnEntity(new EntityGhostBall(owner, isMaxChannel));
                    setOnCooldown();
                    endChannel();
                }
            }

            @Override
            protected void endChannel() {
                super.endChannel();
                isMaxChannel = false;
                owner.maxVelocity = 5;
            }

            @Override
            protected void onMaxChannel() {
                isMaxChannel = true;

                Particle particle = chargedGenerator.generateCircleAway(getMouthPos(), 15, 10);

                level.spawnParticle(particle);
            }

            @Override
            protected void onChannelProgress(int currentState) {

                owner.maxVelocity = com.alientome.core.util.Util.roundClosest(Math.max(5 - currentState / 10d, 2), 0.25);

                Particle particle = chargingGenerator.generateCircleTowards(getMouthPos(), 5, 15, 10);

                level.spawnParticle(particle);
            }

            private Vec2 getMouthPos() {
                if (owner.facing == Direction.LEFT)
                    return new Vec2(10, 18).add(owner.getPos());
                else
                    return new Vec2(30, 18).add(owner.getPos());
            }
        };
    }

    private ChanneledAbility createDashAbility() {

        return new NoActChanneledAbility(this, 33, 7) {

            private Direction dashDirection;

            @Override
            public void startChannel() {
                if (isOffCooldown()) {
                    dashDirection = owner.facing;
                    owner.onGround = false;
                    owner.velocity.setY(0);
                    owner.move(dashDirection, owner.maxVelocity);
                    owner.setAffectedByGravity(false);
                    super.startChannel();
                }
            }

            @Override
            public void stopChannel() {
            }

            @Override
            protected void endChannel() {
                super.endChannel();
                owner.setAffectedByGravity(true);
                setOnCooldown();
            }

            @Override
            protected void onMaxChannel() {
                endChannel();
            }

            @Override
            protected void onChannelProgress(int currentState) {
                owner.facing = dashDirection;
                owner.velocity.setX(dashDirection.normal.getX() * 10);
            }
        };
    }
}
